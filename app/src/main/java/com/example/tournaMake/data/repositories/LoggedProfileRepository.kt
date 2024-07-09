package com.example.tournaMake.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey

class LoggedProfileRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val LOGGED_EMAIL = stringPreferencesKey("logged_email")
    }
    val email = dataStore.data

    suspend fun setEmail(email: String) =
        dataStore.edit { it[LOGGED_EMAIL] = email }
}