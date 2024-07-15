package com.example.tournaMake.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.ui.screens.match.MatchScreen
import com.example.tournaMake.ui.screens.match.testTeam1
import com.example.tournaMake.ui.screens.match.testTeam2
import org.koin.androidx.compose.koinViewModel

class MatchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val state = themeViewModel.state.collectAsStateWithLifecycle()
            MatchScreen(
                state = state.value,
                gameImage = null,
                teamsSet = setOf(testTeam1, testTeam2),
                backFunction = this::goBack
            )
        }
    }

    private fun goBack() {
        val intent = Intent(this, MatchListActivity::class.java)
        startActivity(intent)
    }
}