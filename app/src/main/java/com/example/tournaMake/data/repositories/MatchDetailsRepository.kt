package com.example.tournaMake.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map

/**
 * Used to remember which match was selected in the MatchListScreen
 * */
class MatchDetailsRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val SELECTED_MATCH = stringPreferencesKey("selected_match")
    }

    val selectedMatch = dataStore.data.map { data -> data[SELECTED_MATCH] }

    suspend fun setSelectedMatch(selectedMatchId: String) =
        dataStore.edit { it[SELECTED_MATCH] = selectedMatchId }
}