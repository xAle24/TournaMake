package com.example.tournaMake.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map

/**
 * Stores tournament ID in shared preferences
 * */
class TournamentIDRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val TOURNAMENT_ID = stringPreferencesKey("tournamentID")
    }

    val tournamentID = dataStore.data.map { data -> data[TOURNAMENT_ID] }

    suspend fun setTournamentID(id: String) {
        dataStore.edit { it[TOURNAMENT_ID] = id }
    }
}