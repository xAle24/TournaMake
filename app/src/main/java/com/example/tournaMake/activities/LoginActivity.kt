package com.example.tournaMake.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tournaMake.data.models.AuthenticationViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.ui.screens.login.LoginScreen
import org.koin.androidx.compose.koinViewModel

class LoginActivity : ComponentActivity() {
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
            val authenticationViewModel = koinViewModel<AuthenticationViewModel>()
            val userEmail = authenticationViewModel.loggedEmail.collectAsStateWithLifecycle()
            val userPassword = authenticationViewModel.password.collectAsStateWithLifecycle()
            val rememberMe = authenticationViewModel.rememberMe.collectAsStateWithLifecycle()
            LoginScreen(
                state = state.value,
                navigateToMenu = this::navigateToMenu,
                changeViewModelRememberMeCallback = { authenticationViewModel.setRememberMe(it)},
                rememberMeFromViewModel = rememberMe.value,
                userEmail = userEmail.value.loggedProfileEmail,
                userPassword = userPassword.value
            )
        }
    }

    private fun navigateToMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }

    private fun rememberUser(email: String, password: String, viewModel: AuthenticationViewModel) {

    }
}