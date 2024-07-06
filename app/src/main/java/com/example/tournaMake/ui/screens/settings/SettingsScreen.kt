package com.example.tournaMake.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme
import com.example.tournaMake.ui.theme.LocalBackgroundImageId
import com.example.tournaMake.ui.theme.getBackgroundImageId

/**
 * Builds the Settings Screen. Called in SettingsActivity.
 * */
@Composable
fun SettingsScreen(
    state: ThemeState, // The state of the UI (dark, light or system)
    changeTheme: (ThemeEnum) -> Unit, // The method to change the state, defined in SettingsViewModel
    isSystemInDarkModeCustom: () -> Boolean // see SettingsActivity.kt
) {
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
                        ThemeEnum.System -> if (isSystemInDarkModeCustom()) ThemeEnum.Light else ThemeEnum.Dark
                    }
                    changeTheme(newTheme) // callback passed as parameter to this function
                },
                modifier = Modifier.fillMaxWidth(0.8f).height(60.dp)
            ) {
                // Button Content
                Text(text = "Toggle Theme")
            }
        }
    }
}