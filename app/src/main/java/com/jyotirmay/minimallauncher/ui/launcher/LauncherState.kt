package com.jyotirmay.minimallauncher.ui.launcher

import com.jyotirmay.minimallauncher.data.model.LauncherApp

/**
 * Sealed interface representing the current launcher mode.
 */
sealed interface LauncherMode {
    data object Home : LauncherMode
    data class Letter(val letter: Char) : LauncherMode
    data object Search : LauncherMode
}

/**
 * Complete launcher UI state.
 */
data class LauncherState(
    val mode: LauncherMode = LauncherMode.Home,
    val allApps: List<LauncherApp> = emptyList(),
    val groupedApps: Map<Char, List<LauncherApp>> = emptyMap(),
    val availableLetters: Set<Char> = emptySet(),
    val favouritePackages: Set<String> = emptySet(),
    val favouriteApps: List<LauncherApp> = emptyList(),
    val selectedLetter: Char? = null,
    val fingerY: Float? = null,
    val isAlphabetActive: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<LauncherApp> = emptyList(),
    val isSearchVisible: Boolean = false
)
