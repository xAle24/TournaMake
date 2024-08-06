package com.example.tournaMake.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * This class will be used to store to shared preferences the username and the password
 * of the last main profile user that logged in and opted to be remembered.
 * */
class AuthenticationRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val LOGGED_EMAIL = stringPreferencesKey("logged_email")
        private val USER_PASSWORD = stringPreferencesKey("password")
        private val DOES_USER_WANT_TO_BE_REMEMBERED = booleanPreferencesKey("doesUserWantToBeRemembered")
    }
    /*
    * If you forget to map the value using the companion object, than you obtain a strange
    * representation like this:
    * {
    *   logged_email = alin@bella
    * }
    * */
    val email = dataStore.data.map { data -> data[LOGGED_EMAIL] }
    val password = dataStore.data.map { data -> data[USER_PASSWORD] }
    val doesUserWantToBeRemembered = dataStore.data.map { data -> data[DOES_USER_WANT_TO_BE_REMEMBERED] }

    /**
     * This function should be called only if the user wants the app to remember their credentials.
     * */
    suspend fun setEmail(email: String) =
        dataStore.edit { it[LOGGED_EMAIL] = email }

    /**
     * This function should be called only if the user wants the app to remember their credentials.
     * */
    suspend fun setPassword(password: String) {
        dataStore.edit { it[USER_PASSWORD] = password }
    }

    suspend fun setRememberMe(rememberMe: Boolean) {
        dataStore.edit { it[DOES_USER_WANT_TO_BE_REMEMBERED] = rememberMe }
    }
}