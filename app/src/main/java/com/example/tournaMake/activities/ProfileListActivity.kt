package com.example.tournaMake.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.ProfileListViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.GuestProfile
import com.example.tournaMake.ui.screens.profile.ProfileListScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel

class ProfileListActivity : ComponentActivity() {
    private var appDatabase: AppDatabase? = get<AppDatabase>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // See ThemeViewModel.kt
            val themeViewModel = koinViewModel<ThemeViewModel>()
            // The following line converts the StateFlow contained in the ViewModel
            // to a State object. State objects can trigger recompositions, while
            // StateFlow objects can't. The 'withLifecycle' part ensures this state
            // is destroyed when we leave this Activity.
            val state = themeViewModel.state.collectAsStateWithLifecycle()
            // View Model of profile list
            val profileListViewModel = koinViewModel<ProfileListViewModel>()
            fetchAndUpdateGuestProfile(profileListViewModel)

            ProfileListScreen(
                state = state.value,
                profileListViewModel.profileNamesListLiveData,
                backButton = this::backButton
            )
        }
    }

    private fun fetchAndUpdateGuestProfile(profileListViewModel: ProfileListViewModel) {
        var guestProfiles: List<GuestProfile> = emptyList()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                guestProfiles = appDatabase?.guestProfileDao()?.getAll() ?: emptyList()
                Log.d("DEV", "In fetchAndUpdateGuestProfile() in ProfileListActivity, guestProfiles = ${guestProfiles.forEach{ profile -> profile.username}}")
                profileListViewModel.changeProfileNamesList(guestProfiles.map { it.username })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun backButton() {
        finish()
    }
}