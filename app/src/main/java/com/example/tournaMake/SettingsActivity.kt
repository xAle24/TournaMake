package com.example.tournaMake

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tournaMake.ui.theme.Theme
import com.example.tournaMake.ui.theme.TournaMakeTheme
import com.example.tournaMake.ui.theme.LocalBackgroundImageId
import com.example.tournaMake.ui.theme.getBackgroundImageId

class SettingsActivity : ComponentActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //val themeViewModel = viewModel<SettingsViewModel>()
            var selectedTheme by remember { mutableStateOf(Theme.System) }
            TournaMakeTheme(
                darkTheme = when (selectedTheme) {
                    Theme.Light -> false
                    Theme.Dark -> true
                    Theme.System -> isSystemInDarkTheme()
                }
            ) {
                val backgroundImageId = getBackgroundImageId( // defined in ThemeUtilities.kt
                    darkMode = selectedTheme == Theme.Dark || (selectedTheme == Theme.System && isSystemInDarkModeCustom())
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
                            painter = painterResource(id = backgroundImageId),
                            contentDescription = "Background Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Column(
                            modifier = Modifier.fillMaxSize()
                                .wrapContentSize(Alignment.Center)
                        ) {
                            Button(
                                onClick = {
                                    selectedTheme = when (selectedTheme) {
                                        Theme.Light -> Theme.Dark
                                        Theme.Dark -> Theme.Light
                                        Theme.System -> if (isSystemInDarkModeCustom()) Theme.Light else Theme.Dark
                                    }
                                }
                            ) {
                                // Button Content
                                Text(text = "Toggle Theme")
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Code taken from here: https://stackoverflow.com/questions/44170028/android-how-to-detect-if-night-mode-is-on-when-using-appcompatdelegate-mode-ni
     * This function must stay in a class that extends ComponentActivity, otherwise the configuration
     * and context are not available.
     * */
    private fun isSystemInDarkModeCustom(): Boolean {
        val nightModeFlags: Int =
            resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)
        return when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }
}