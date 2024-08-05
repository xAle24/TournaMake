package com.example.tournaMake.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.AuthenticationViewModel
import com.example.tournaMake.data.models.LoginStatus
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.ui.screens.login.LoginScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel
import org.koin.core.context.GlobalContext.get

class LoginActivity : ComponentActivity() {
    private val appDatabase = get<AppDatabase>()
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
            val loginState = authenticationViewModel.loginStatus.collectAsStateWithLifecycle()

            Log.d("DEV-LOGIN", "Login status: ${loginState.value}")
            if (loginState.value == LoginStatus.Success) {
                navigateToMenu()
                Log.d("DEV-LOGIN", "navigateToMenu() theoretically called")
                finish() // TODO: maybe this is useless
            } else {
                LoginScreen(
                    state = state.value,
                    changeViewModelRememberMeCallback = { authenticationViewModel.setRememberMe(it) },
                    rememberMeFromViewModel = rememberMe.value,
                    userEmail = userEmail.value.loggedProfileEmail,
                    userPassword = userPassword.value,
                    handleLogin = this::handleLogin
                )
            }
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
        viewModel: AuthenticationViewModel = get<AuthenticationViewModel>()
    ) {
        lifecycleScope.launch(Dispatchers.Default) {
            try {
                Log.d("DEV", "Checking email $email, password $password")
                val storedPassword = appDatabase.mainProfileDao().checkPassword(email)
                Log.d("DEV", "Retrieved password = $storedPassword")
                if (storedPassword == password) {
                    viewModel.changeLoginStatus(LoginStatus.Success)
                    Log.d("DEV", "SUCCESS")
                    if (rememberMe) {
                        viewModel.saveUserAuthenticationPreferences(email, password, true)
                    }
                } else {
                    viewModel.changeLoginStatus(LoginStatus.Fail)
                    Log.d("DEV", "Fail...")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                viewModel.changeLoginStatus(LoginStatus.Fail)
            }

            // Toasts and UI updates can only be executed on the main thread
            withContext(Dispatchers.Main) {
                when (viewModel.loginStatus.value) {
                    LoginStatus.Success -> Toast.makeText(
                        this@LoginActivity,
                        "Login succeeded",
                        Toast.LENGTH_SHORT
                    ).show()

                    LoginStatus.Fail -> Toast.makeText(
                        this@LoginActivity,
                        "Login failed",
                        Toast.LENGTH_SHORT
                    ).show()

                    LoginStatus.Unknown -> {}
                }
            }

            //Trying to switch activity
            if (viewModel.loginStatus.value == LoginStatus.Success) {
                Log.d("DEV-AHGAH", "Entering this if")
                val intent = Intent(this@LoginActivity, MenuActivity::class.java)
                startActivity(intent)
            }
        }
    }
}