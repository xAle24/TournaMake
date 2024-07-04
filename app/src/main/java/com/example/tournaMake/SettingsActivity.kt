package com.example.tournaMake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
            var themeState by remember { mutableStateOf(Theme.System) }
            TournaMakeTheme(
                darkTheme = when (themeState) {
                    Theme.Light -> false
                    Theme.Dark -> true
                    Theme.System -> isSystemInDarkTheme()
                }
            ) {
                Button(onClick = {
                    //SettingsViewModel.changeTheme(themeState)
                }) {
                    Text(text = "Toggle Dark Mode")
                }
            }
        }
    }
}