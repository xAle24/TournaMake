package com.example.tournaMake.ui.screens.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme

@Composable
fun MenuScreen(
    state: ThemeState,
    navigateToSettings: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToTournament: () -> Unit,
    navigateToFavorites: () -> Unit
) {
    BasicScreenWithTheme(
        state = state,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { navigateToTournament() }, modifier = Modifier.fillMaxWidth(0.8f).height(60.dp)) {
                Text("Start")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navigateToFavorites() }, modifier = Modifier.fillMaxWidth(0.8f).height(60.dp)) {
                Text("Favorites")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navigateToProfile() }, modifier = Modifier.fillMaxWidth(0.8f).height(60.dp)) {
                Text("Profile")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navigateToSettings() }, modifier = Modifier.fillMaxWidth(0.8f).height(60.dp)) {
                Text("Settings")
            }
        }
    }
}