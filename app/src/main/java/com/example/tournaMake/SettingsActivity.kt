package com.example.tournaMake

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tournaMake.ui.screens.settings.SettingsScreen
import com.example.tournaMake.data.models.ThemeViewModel

class SettingsActivity : ComponentActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // See ThemeViewModel.kt
            val themeViewModel = viewModels<ThemeViewModel>()
            // The following line converts the StateFlow contained in the ViewModel
            // to a State object. State objects can trigger recompositions, while
            // StateFlow objects can't. The 'withLifecycle' part ensures this state
            // is destroyed when we leave this Activity.
            val state = themeViewModel.value.state.collectAsStateWithLifecycle()
            SettingsScreen( // see SettingsScreen.kt in package ui.screens.settings
                state = state.value,
                changeTheme = themeViewModel.value::changeTheme,
                isSystemInDarkModeCustom = this::isSystemInDarkModeCustom
            )
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