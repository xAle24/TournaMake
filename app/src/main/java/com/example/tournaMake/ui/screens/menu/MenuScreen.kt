package com.example.tournaMake.ui.screens.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tournaMake.R
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme

@Composable
fun MenuScreen(
    state: ThemeState,
    navigateToSettings: () -> Unit,
    navigateToListProfile: () -> Unit,
    navigateToTournament: () -> Unit,
    navigateToFavorites: () -> Unit,
    navigateToProfile: () -> Unit
) {
    BasicScreenWithTheme(
        state = state,
    ) {
        val imageLogoId = if (state.theme == ThemeEnum.Dark) R.drawable.light_writings else R.drawable.dark_writings
        val imageKnightId = if (state.theme == ThemeEnum.Dark) R.drawable.light_knight else R.drawable.dark_knight
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { /* Handle left button click */ }) {
                    Text("Logout")
                }
                Button(onClick = { navigateToProfile() }) {
                    Text("My Profile")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = imageLogoId),
                contentDescription = "Appropriate logo image",
                modifier = Modifier.fillMaxWidth(0.8f).fillMaxHeight(0.2f)
            )
            Button(onClick = { navigateToTournament() }, modifier = Modifier.fillMaxWidth(0.8f).height(60.dp)) {
                Text("Start")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navigateToFavorites() }, modifier = Modifier.fillMaxWidth(0.8f).height(60.dp)) {
                Text("Favorites")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navigateToListProfile() }, modifier = Modifier.fillMaxWidth(0.8f).height(60.dp)) {
                Text("Profile")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navigateToSettings() }, modifier = Modifier.fillMaxWidth(0.8f).height(60.dp)) {
                Text("Settings")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = imageKnightId),
                contentDescription = "Appropriate theme image",
                modifier = Modifier.fillMaxWidth(0.8f).fillMaxHeight(0.5f)
            )
        }
    }
}