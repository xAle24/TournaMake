package com.example.tournaMake.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.ui.screens.match.MatchCreationScreen
import org.koin.androidx.compose.koinViewModel

class MatchCreationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val state = themeViewModel.state.collectAsStateWithLifecycle()
            MatchCreationScreen(
                state = state.value,
                backFunction = this::goBack,
                navigateToMatch = this::navigateToMatch
            )
        }
    }

    private fun navigateToMatch() {
        val intent = Intent(this, MatchActivity::class.java)
        startActivity(intent)
    }
    private fun goBack() {
        finish()
    }
}