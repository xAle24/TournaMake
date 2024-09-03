package com.example.tournaMake.activities

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.CoordinatesViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.utils.Coordinates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

fun updateDatabaseWithPhotoUri(
    uri: Uri,
    loggedEmail: String,
    owner: LifecycleOwner
) {
    val appDatabase by inject<AppDatabase>(AppDatabase::class.java)
    Log.d("DEV", "Got logged email: $loggedEmail")
    owner.lifecycleScope.launch(Dispatchers.IO) {
        val mainProfile = appDatabase.mainProfileDao().getProfileByEmail(loggedEmail)
        val updatedMainProfile = MainProfile(
            username = mainProfile.username,
            password = mainProfile.password,
            email = mainProfile.email,
            profileImage = uri.toString(),
            wonTournamentsNumber = mainProfile.wonTournamentsNumber,
            locationLatitude = mainProfile.locationLatitude,
            locationLongitude = mainProfile.locationLongitude
        )
        appDatabase.mainProfileDao().upsert(updatedMainProfile)
    }
}

fun updateDatabaseWithCoordinates(
    loggedEmail: String,
    latitude: Double,
    longitude: Double,
    coordinatesViewModel: CoordinatesViewModel,
    owner: LifecycleOwner
) {
    Log.d(
        "DEV",
        "In RegistrationPhotoActivity, coordinates are: latitude = $latitude, longitude = $longitude"
    )
    val appDatabase by inject<AppDatabase>(AppDatabase::class.java)
    try {
        owner.lifecycleScope.launch(Dispatchers.IO) {
            val mainProfile = appDatabase.mainProfileDao().getProfileByEmail(loggedEmail)
            val updatedMainProfile = MainProfile(
                username = mainProfile.username,
                password = mainProfile.password,
                email = mainProfile.email,
                profileImage = mainProfile.profileImage,
                wonTournamentsNumber = mainProfile.wonTournamentsNumber,
                locationLatitude = latitude,
                locationLongitude = longitude
            )
            appDatabase.mainProfileDao().upsert(updatedMainProfile)
            coordinatesViewModel.changeCoordinates(Coordinates(latitude, longitude))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}