package com.jyotirmay.minimallauncher.domain

import com.jyotirmay.minimallauncher.data.model.LauncherApp

/**
 * Groups a sorted list of apps by their first alphabetic letter.
 * Pure function – no Android dependencies, fully testable.
 */
object AppGrouper {

    /**
     * Groups [apps] by their [LauncherApp.letterKey].
     * Returns a map with entries for every letter A–Z (some may be empty lists).
     * Apps within each group are sorted alphabetically.
     */
    fun groupAppsByLetter(apps: List<LauncherApp>): Map<Char, List<LauncherApp>> {
        val grouped = apps.groupBy { it.letterKey }
        // Ensure every letter A–Z is present (empty list for letters with no apps)
        val result = linkedMapOf<Char, List<LauncherApp>>()
        for (c in 'A'..'Z') {
            result[c] = (grouped[c] ?: emptyList()).sortedBy { it.label.lowercase() }
        }
        // '#' group for non-alpha if any
        grouped['#']?.let { result['#'] = it.sortedBy { app -> app.label.lowercase() } }
        return result
    }

    /**
     * Returns the set of letters that have at least one app.
     */
    fun availableLetters(groupedApps: Map<Char, List<LauncherApp>>): Set<Char> {
        return groupedApps.filter { it.value.isNotEmpty() }.keys
    }
}
