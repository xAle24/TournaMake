package com.example.tournaMake.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.tournaMake.data.models.ThemeEnum
import kotlinx.coroutines.flow.map

class ThemeRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val THEME_KEY = stringPreferencesKey("theme")
    }
    val theme = dataStore.data
        .map { preferences ->
            try {
                ThemeEnum.valueOf(preferences[THEME_KEY] ?: "System")
            } catch (_: Exception) {
                ThemeEnum.System
            }
        }

    suspend fun setTheme(theme: ThemeEnum) =
        dataStore.edit { it[THEME_KEY] = theme.toString() }
}