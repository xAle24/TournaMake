package com.example.tournaMake.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.tournaMake.sampledata.AppDatabase
import kotlinx.coroutines.flow.map

class GameDetailsRepository(private val appDatabase: AppDatabase, private val dataStore: DataStore<Preferences>) {
    companion object {
        private val SELECTED_GAME = stringPreferencesKey("selected_game")
    }

    val selectedGame = dataStore.data.map { data -> data[SELECTED_GAME] }

    suspend fun setSelectedGame(selectedGameId: String) =
        dataStore.edit { it[SELECTED_GAME] = selectedGameId }

    fun getGameDetails(gameID: String) = appDatabase.gameDao().getGameFromID(gameID)

    fun getMainHighScores(gameID: String) = appDatabase.gameDao().getHighScoreMain(gameID)
    fun getGuestHighScores(gameID: String) = appDatabase.gameDao().getHighScoreGuest(gameID)

}