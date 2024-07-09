package com.example.tournaMake.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.ui.screens.registration.RegistrationScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get // correct koin import to use the get function for single objects
import org.koin.androidx.compose.koinViewModel

class RegisterActivity : ComponentActivity() {
    /* This line provides the database through dependency injection with Koin.
    * TODO: consider adding a repository between the Room database and the DAOs, it's a best practice
    * */
    private var appDatabase: AppDatabase? = get<AppDatabase>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // See ThemeViewModel.kt
            val themeViewModel = koinViewModel<ThemeViewModel>()
            // The following line converts the StateFlow contained in the ViewModel
            // to a State object. State objects can trigger recompositions, while
            // StateFlow objects can't. The 'withLifecycle' part ensures this state
            // is destroyed when we leave this Activity.
            val state = themeViewModel.state.collectAsStateWithLifecycle()
            RegistrationScreen(
                state = state.value,
                handleRegistration = this::handleRegistration // TODO: verify if this still works
            )
        }
    }

    private fun handleRegistration(username: String, password: String, email: String) {
        val mainProfile = MainProfile(email, username, password, "", 0, 0f, 0f)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Log.d("DEV", "Ci sono")
                appDatabase?.mainProfileDao()?.insert(mainProfile)
                //val intent = Intent(this, RegistrationPhotoActivity::class.java)
                //startActivity(intent)
                goToNextActivity()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun goToNextActivity() {
        val intent = Intent(this, RegistrationPhotoActivity::class.java)
        startActivity(intent)
    }
}