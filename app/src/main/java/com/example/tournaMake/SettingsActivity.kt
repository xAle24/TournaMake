package com.example.tournaMake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.tournaMake.ui.theme.Theme
import com.example.tournaMake.ui.theme.TournaMakeTheme
import com.example.tournaMake.ui.screens.settings.SettingsViewModel

class SettingsActivity : ComponentActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            //val themeViewModel = viewModel<SettingsViewModel>()
            var selectedTheme by remember { mutableStateOf(Theme.System) }
            TournaMakeTheme(
                darkTheme = when (selectedTheme) {
                    Theme.Light -> false
                    Theme.Dark -> true
                    Theme.System -> isSystemInDarkTheme()
                }
            ) {
                Button(
                    onClick = {
                        selectedTheme = when (selectedTheme) {
                            Theme.Light -> Theme.Dark
                            Theme.Dark -> Theme.Light
                            Theme.System -> Theme.Dark
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