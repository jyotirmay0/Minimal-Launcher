package com.jyotirmay.minimallauncher.persistence

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "favourites")

/**
 * Persists favourite app package names using DataStore Preferences.
 * Stores only package names (serializable strings), never Drawables or other non-serializable objects.
 */
class FavoritesStore(private val context: Context) {

    companion object {
        private val FAVOURITES_KEY = stringSetPreferencesKey("favourite_packages")
    }

    val favourites: Flow<Set<String>> = context.dataStore.data
        .map { prefs ->
            prefs[FAVOURITES_KEY] ?: emptySet()
        }

    suspend fun toggleFavourite(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[FAVOURITES_KEY]?.toMutableSet() ?: mutableSetOf()
            if (current.contains(packageName)) {
                current.remove(packageName)
            } else {
                current.add(packageName)
            }
            prefs[FAVOURITES_KEY] = current
        }
    }

    suspend fun removeFavourite(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[FAVOURITES_KEY]?.toMutableSet() ?: return@edit
            current.remove(packageName)
            prefs[FAVOURITES_KEY] = current
        }
    }

    suspend fun removeStalePackages(installedPackages: Set<String>) {
        context.dataStore.edit { prefs ->
            val current = prefs[FAVOURITES_KEY] ?: return@edit
            val filtered = current.filter { it in installedPackages }.toSet()
            if (filtered.size != current.size) {
                prefs[FAVOURITES_KEY] = filtered
            }
        }
    }
}
