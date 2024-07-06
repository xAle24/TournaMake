package com.example.tournaMake.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.ui.screens.menu.MenuScreen

class MenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // See ThemeViewModel.kt
            val themeViewModel = viewModels<ThemeViewModel>()
            // The following line converts the StateFlow contained in the ViewModel
            // to a State object. State objects can trigger recompositions, while
            // StateFlow objects can't. The 'withLifecycle' part ensures this state
            // is destroyed when we leave this Activity.
            val state = themeViewModel.value.state.collectAsStateWithLifecycle()
            MenuScreen(
                state = state.value,
                navigateToTournament= this::navigateToTournament,
                navigateToProfile = this::navigateToProfile,
                navigateToSettings = this::navigateToSettings,
                navigateToFavorites = this::navigateToFavorites
            )
        }
    }
    private fun navigateToTournament() {
        val intent = Intent(this, TournamentActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToFavorites() {
        val intent = Intent(this, FavoritesActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}