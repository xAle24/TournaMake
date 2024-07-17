package com.example.tournaMake.data.repositories

import android.graphics.Bitmap
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit

// Used to save just the URI to the profile picture, not the bitmap itself.
class ProfileImageRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val PROFILE_IMAGE_URI = stringPreferencesKey("profile_image")
    }

    val profileImageUri = dataStore.data.map { data -> data[PROFILE_IMAGE_URI] }

    suspend fun setProfileImageUri(imageURI: Uri) {
        dataStore.edit { it[PROFILE_IMAGE_URI] = imageURI.toString() }
    }
}