package com.jyotirmay.minimallauncher.ui.launcher

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Root launcher screen. All content swaps happen within this single Compose hierarchy
 * to avoid black flashes or layout jumps.
 */
@Composable
fun LauncherScreen(
    viewModel: LauncherViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    // Handle back press in search mode
    BackHandler(enabled = state.isSearchVisible) {
        viewModel.closeSearch()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
    ) {
        when (state.mode) {
            is LauncherMode.Home -> {
                HomeContent(
                    favouriteApps = state.favouriteApps,
                    favouritePackages = state.favouritePackages,
                    onAppTap = { viewModel.launchApp(it) },
                    onAppLongPress = { viewModel.toggleFavourite(it.packageName) },
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectVerticalDragGestures(
                                onVerticalDrag = { _, dragAmount ->
                                    if (dragAmount < -30f) {
                                        viewModel.openSearch()
                                    }
                                }
                            )
                        }
                )
            }

            is LauncherMode.Letter -> {
                val letter = (state.mode as LauncherMode.Letter).letter
                val apps = state.groupedApps[letter] ?: emptyList()
                FilteredAppsContent(
                    letter = letter,
                    apps = apps,
                    favouritePackages = state.favouritePackages,
                    onAppTap = { viewModel.launchApp(it) },
                    onAppLongPress = { viewModel.toggleFavourite(it.packageName) }
                )
            }

            is LauncherMode.Search -> {
                SearchScreen(
                    query = state.searchQuery,
                    results = state.searchResults,
                    favouritePackages = state.favouritePackages,
                    onQueryChange = { viewModel.onSearchQueryChanged(it) },
                    onAppTap = { viewModel.launchApp(it) },
                    onAppLongPress = { viewModel.toggleFavourite(it.packageName) },
                    onClose = { viewModel.closeSearch() }
                )
            }
        }

        // Alphabet bar always visible (except in search)
        if (state.mode !is LauncherMode.Search) {
            AlphabetBar(
                availableLetters = state.availableLetters,
                selectedLetter = state.selectedLetter,
                fingerY = state.fingerY,
                isActive = state.isAlphabetActive,
                onTouchStart = { letter, y ->
                    viewModel.onAlphabetTouchStart(letter, y)
                },
                onDrag = { letter, y ->
                    viewModel.onAlphabetDrag(letter, y)
                },
                onRelease = {
                    viewModel.onAlphabetRelease()
                },
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}
