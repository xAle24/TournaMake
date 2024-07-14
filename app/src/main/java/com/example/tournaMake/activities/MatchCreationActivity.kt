package com.example.tournaMake.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tournaMake.R
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.ui.screens.common.BasicScreenWithAppBars
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
                backFunction = this::goBack
            )
        }
    }

    private fun goBack() {
        finish()
    }
}