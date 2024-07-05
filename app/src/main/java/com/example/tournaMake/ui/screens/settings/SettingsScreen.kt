package com.example.tournaMake.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.tournaMake.data.models.ThemeEnum
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
    val backgroundImageId = getBackgroundImageId( // defined in ThemeUtilities.kt
        darkMode = state.theme == ThemeEnum.Dark || (state.theme == ThemeEnum.System && isSystemInDarkModeCustom())
    )
    /* CompositionLocalProvider is a Composable (so it has the usual structure
    * Composable (params) { content }
    * ), which is used every time we want the background image according to the
    * current theme of the app. */
    CompositionLocalProvider(LocalBackgroundImageId provides backgroundImageId) {
        // Encapsulating the buttons in a Box, that fills the whole background
        // and can therefore be coloured as wanted.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .wrapContentSize(Alignment.TopCenter)
        ) {
            Image(
                painter = painterResource(id = backgroundImageId), // provided by the CompositionLocalProvider
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
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
                    }
                ) {
                    // Button Content
                    Text(text = "Toggle Theme")
                }
            }
        }
    }
}