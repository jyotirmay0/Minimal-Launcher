package com.jyotirmay.minimallauncher.ui.launcher

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jyotirmay.minimallauncher.data.model.LauncherApp
import com.jyotirmay.minimallauncher.data.repository.AppRepository
import com.jyotirmay.minimallauncher.domain.AppGrouper
import com.jyotirmay.minimallauncher.persistence.FavoritesStore
import com.jyotirmay.minimallauncher.receiver.PackageChangeReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val appRepository = AppRepository(application)
    private val favoritesStore = FavoritesStore(application)

    private val _state = MutableStateFlow(LauncherState())
    val state: StateFlow<LauncherState> = _state.asStateFlow()

    init {
        loadApps()
        observeFavourites()
        setupPackageChangeListener()
    }

    private fun loadApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val apps = appRepository.getApps()
            val grouped = AppGrouper.groupAppsByLetter(apps)
            val available = AppGrouper.availableLetters(grouped)

            _state.update { current ->
                val favApps = buildFavouriteApps(apps, current.favouritePackages)
                current.copy(
                    allApps = apps,
                    groupedApps = grouped,
                    availableLetters = available,
                    favouriteApps = favApps
                )
            }

            // Clean up stale favourites
            val installedPackages = apps.map { it.packageName }.toSet()
            favoritesStore.removeStalePackages(installedPackages)
        }
    }

    private fun observeFavourites() {
        viewModelScope.launch {
            favoritesStore.favourites.collect { favPackages ->
                _state.update { current ->
                    val favApps = buildFavouriteApps(current.allApps, favPackages)
                    current.copy(
                        favouritePackages = favPackages,
                        favouriteApps = favApps
                    )
                }
            }
        }
    }

    private fun buildFavouriteApps(
        allApps: List<LauncherApp>,
        favouritePackages: Set<String>
    ): List<LauncherApp> {
        if (favouritePackages.isEmpty()) {
            // Default: show first 7 apps if no favourites set
            return allApps.take(7)
        }
        return allApps.filter { it.packageName in favouritePackages }.take(7)
    }

    private fun setupPackageChangeListener() {
        PackageChangeReceiver.onPackageChanged = {
            viewModelScope.launch(Dispatchers.IO) {
                appRepository.invalidateCache()
                val apps = appRepository.refreshApps()
                val grouped = AppGrouper.groupAppsByLetter(apps)
                val available = AppGrouper.availableLetters(grouped)

                _state.update { current ->
                    val favApps = buildFavouriteApps(apps, current.favouritePackages)
                    current.copy(
                        allApps = apps,
                        groupedApps = grouped,
                        availableLetters = available,
                        favouriteApps = favApps
                    )
                }

                // Clean up stale favourites
                val installedPackages = apps.map { it.packageName }.toSet()
                favoritesStore.removeStalePackages(installedPackages)
            }
        }
    }

    // --- Alphabet interaction ---

    fun onAlphabetTouchStart(letter: Char, fingerY: Float) {
        _state.update {
            it.copy(
                mode = LauncherMode.Letter(letter),
                selectedLetter = letter,
                fingerY = fingerY,
                isAlphabetActive = true,
                isSearchVisible = false,
                searchQuery = ""
            )
        }
    }

    fun onAlphabetDrag(letter: Char, fingerY: Float) {
        _state.update {
            it.copy(
                mode = LauncherMode.Letter(letter),
                selectedLetter = letter,
                fingerY = fingerY
            )
        }
    }

    fun onAlphabetRelease() {
        _state.update {
            it.copy(
                mode = LauncherMode.Home,
                selectedLetter = null,
                fingerY = null,
                isAlphabetActive = false
            )
        }
    }

    // --- Favourites ---

    fun toggleFavourite(packageName: String) {
        viewModelScope.launch {
            favoritesStore.toggleFavourite(packageName)
        }
    }

    // --- Search ---

    fun openSearch() {
        _state.update {
            it.copy(
                mode = LauncherMode.Search,
                isSearchVisible = true,
                searchQuery = "",
                searchResults = it.allApps
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _state.update { current ->
            val results = if (query.isBlank()) {
                current.allApps
            } else {
                current.allApps.filter {
                    it.label.contains(query, ignoreCase = true)
                }
            }
            current.copy(
                searchQuery = query,
                searchResults = results
            )
        }
    }

    fun closeSearch() {
        _state.update {
            it.copy(
                mode = LauncherMode.Home,
                isSearchVisible = false,
                searchQuery = "",
                searchResults = emptyList()
            )
        }
    }

    // --- App launching ---

    fun launchApp(app: LauncherApp) {
        try {
            val context = getApplication<Application>()
            val intent = if (app.activityName != null) {
                Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    component = ComponentName(app.packageName, app.activityName)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                }
            } else {
                context.packageManager.getLaunchIntentForPackage(app.packageName)
            }
            intent?.let { context.startActivity(it) }
        } catch (e: Exception) {
            // Handle gracefully - app may have been uninstalled
        }
    }

    override fun onCleared() {
        super.onCleared()
        PackageChangeReceiver.onPackageChanged = null
    }
}
