package com.example.tournaMake.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.ui.screens.tournament.TournamentCreationScreen
import org.koin.androidx.compose.koinViewModel

class TournamentCreationActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val state = themeViewModel.state.collectAsStateWithLifecycle()
            TournamentCreationScreen(
                state = state.value,
                navigateToTournament = this::navigateToTournament
            )
        }
    }
    private fun navigateToTournament() {
        val intent = Intent(this, TournamentActivity::class.java)
        startActivity(intent)
    }
}
