package com.example.tournaMake.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.tournaMake.sampledata.AppDatabase
import kotlinx.coroutines.flow.map

class MatchRepository(private val dataStore: DataStore<Preferences>, private val appDatabase: AppDatabase) {
    companion object {
        private val SELECTED_MATCH = stringPreferencesKey("selected_match")
    }

    val selectedMatch = dataStore.data.map { data -> data[SELECTED_MATCH] }

    suspend fun setSelectedMatch(selectedMatchId: String) =
        dataStore.edit { it[SELECTED_MATCH] = selectedMatchId }

    fun getAllMatchesListLiveData() = appDatabase.matchDao().getAllWithGamesNames()

    fun getPlayerHistory(email: String) = appDatabase.matchDao().getMyMatches(email)
}