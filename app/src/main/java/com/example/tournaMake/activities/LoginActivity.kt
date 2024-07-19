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
import org.koin.androidx.viewmodel.ext.android.viewModel

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
            LoginScreen(
                state = state.value,
                checkIfUserWantedToBeRemembered = {
                    this.checkIfUserWantedToBeRemembered(authenticationViewModel)
                },
                getRememberedEmail = {
                    this.getRememberedEmail(authenticationViewModel)
                },
                getRememberedPassword = {
                    this.getRememberedPassword(authenticationViewModel)
                },
                handleLogin = { email, password, rememberMe ->
                    handleLogin(
                        email = email,
                        password = password,
                        rememberMe = rememberMe,
                        viewModel = authenticationViewModel
                    )
                }
            )
        }
    }

    private fun checkIfUserWantedToBeRemembered(vm: AuthenticationViewModel): Boolean {
        return vm.didUserWantToBeRemembered()
    }

    private fun getRememberedEmail(vm: AuthenticationViewModel): String {
        return vm.getRememberedEmail().value.loggedProfileEmail
    }

    private fun getRememberedPassword(vm: AuthenticationViewModel): String {
        return vm.getRememberedPassword().value
    }

    private fun navigateToMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }

    private fun handleLogin(
        email: String,
        password: String,
        rememberMe: Boolean,
        viewModel: AuthenticationViewModel
    ) {
        // TODO: add database check to see if user exists
        if (rememberMe) {
            viewModel.rememberEmailAndPassword(email, password)
        }
        navigateToMenu()
    }
}