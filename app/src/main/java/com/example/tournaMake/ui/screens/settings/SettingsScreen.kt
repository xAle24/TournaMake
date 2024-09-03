package com.example.tournaMake.ui.screens.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme
import com.example.tournaMake.ui.theme.LightBlue
import com.example.tournaMake.ui.theme.MediumPurple
import org.koin.androidx.compose.koinViewModel

/**
 * Builds the Settings Screen. Called in SettingsActivity.
 * */
@Composable
fun SettingsScreen() {
    // See ThemeViewModel.kt
    val themeViewModel = koinViewModel<ThemeViewModel>()
    // The following line converts the StateFlow contained in the ViewModel
    // to a State object. State objects can trigger recompositions, while
    // StateFlow objects can't. The 'withLifecycle' part ensures this state
    // is destroyed when we leave this Activity.
    val state by themeViewModel.state.collectAsStateWithLifecycle()
    val isSystemInDarkTheme = isSystemInDarkTheme()
    BasicScreenWithTheme(
        state = state
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            Button(
                onClick = {
                    val newTheme = when (state.theme) {
                        ThemeEnum.Light -> ThemeEnum.Dark
                        ThemeEnum.Dark -> ThemeEnum.Light
                        ThemeEnum.System -> if (isSystemInDarkTheme) ThemeEnum.Light else ThemeEnum.Dark
                    }
                    themeViewModel.changeTheme(newTheme) // callback passed as parameter to this function
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                // Button Content
                Text(text = "Toggle Theme")
            }
        }
    }
}