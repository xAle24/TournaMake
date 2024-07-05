package com.example.tournaMake

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.tournaMake.ui.theme.Theme
import com.example.tournaMake.ui.theme.TournaMakeTheme
import com.example.tournaMake.ui.screens.settings.SettingsViewModel

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
                // Encapsulating the buttons in a Box, that fills the whole background
                // and can therefore be coloured as wanted.
                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .wrapContentSize(Alignment.TopCenter)
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