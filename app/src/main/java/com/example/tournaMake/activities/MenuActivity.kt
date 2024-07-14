package com.example.tournaMake.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.ui.screens.menu.MenuScreen
import org.koin.androidx.compose.koinViewModel

class MenuActivity : ComponentActivity() {
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
            MenuScreen(
                state = state.value,
                navigateToTournament= this::navigateToTournament,
                navigateToListProfile = this::navigateToListProfile,
                navigateToSettings = this::navigateToSettings,
                navigateToGamesList = this::navigateToGamesList,
                navigateToMatchesList = this::navigateToMatchesList
            )
        }
    }
    private fun navigateToTournament() {
        val intent = Intent(this, TournamentListActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToGamesList() {
        val intent = Intent(this, GamesListActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToListProfile() {
        val intent = Intent(this, ProfileListActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToMatchesList() {
        val intent = Intent(this, MatchListActivity::class.java)
        startActivity(intent)
    }
}