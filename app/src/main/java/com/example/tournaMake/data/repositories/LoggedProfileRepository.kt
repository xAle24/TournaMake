package com.example.tournaMake.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map

class LoggedProfileRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val LOGGED_EMAIL = stringPreferencesKey("logged_email")
    }
    /*
    * If you forget to map the value using the companion object, than you obtain a strange
    * representation like this:
    * {
    *   logged_email = alin@bella
    * }
    * */
    val email = dataStore.data.map { data -> data[LOGGED_EMAIL] }

    suspend fun setEmail(email: String) =
        dataStore.edit { it[LOGGED_EMAIL] = email }
}