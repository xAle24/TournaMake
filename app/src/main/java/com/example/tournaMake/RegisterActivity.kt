package com.example.tournaMake

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.ui.screens.registration.RegistrationScreen
import com.example.tournaMake.ui.theme.TournaMakeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : ComponentActivity() {
    private var appDatabase: AppDatabase? = AppDatabase.getDatabase(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // See ThemeViewModel.kt
            val themeViewModel = viewModels<ThemeViewModel>()
            // The following line converts the StateFlow contained in the ViewModel
            // to a State object. State objects can trigger recompositions, while
            // StateFlow objects can't. The 'withLifecycle' part ensures this state
            // is destroyed when we leave this Activity.
            val state = themeViewModel.value.state.collectAsStateWithLifecycle()
            RegistrationScreen(
                state = state.value,
                isSystemInDarkModeCustom = this::isSystemInDarkModeCustom,
                handleRegistration = this::handleRegistration // TODO: verify if this still works
            )
        }
    }

    private fun handleRegistration(username: String, password: String, email: String) {
        val mainProfile = MainProfile(email, username, password, "", 0, 0f, 0f)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                appDatabase?.mainProfileDao()?.insert(mainProfile)
            } catch (e: Exception) {
                e.printStackTrace()
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
