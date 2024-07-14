package com.example.tournaMake.ui.screens.tournament

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.sampledata.Tournament
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme
import com.example.tournaMake.ui.theme.getThemeColors

@Composable
fun TournamentCreationScreen(
    state: ThemeState,
    navigateToTournament: () -> Unit
) {
    BasicScreenWithTheme(
        state = state
    ) {
        val colorConstants = getThemeColors(themeState = state)
        Button(
            onClick = { navigateToTournament() },
            modifier = Modifier.background(colorConstants.getButtonBackground()),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            )
        ) {
            Column {
                Text("Start tournament")
            }
        }
    }
}