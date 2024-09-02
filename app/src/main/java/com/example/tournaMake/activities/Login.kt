package com.example.tournaMake.activities

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.tournaMake.activities.navgraph.NavigationRoute
import com.example.tournaMake.data.models.AuthenticationViewModel
import com.example.tournaMake.data.models.LoginStatus
import com.example.tournaMake.sampledata.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

fun handleLogin(
    email: String,
    password: String,
    rememberMe: Boolean,
    viewModel: AuthenticationViewModel,
    owner: LifecycleOwner,
    navController: NavController,
    context: Context
) {
    val appDatabase = inject<AppDatabase>(AppDatabase::class.java).value
    owner.lifecycleScope.launch(Dispatchers.Default) {
        try {
            Log.d("DEV", "Checking email $email, password $password")
            val storedPassword = appDatabase.mainProfileDao().checkPassword(email)
            Log.d("DEV", "Retrieved password = $storedPassword")
            if (storedPassword == password) {
                viewModel.changeLoginStatus(LoginStatus.Success)
                Log.d("DEV", "SUCCESS")
                if (rememberMe) {
                    viewModel.saveUserAuthenticationPreferences(email, password, true)
                } else {
                    viewModel.setRememberMe(false)
                }
                //Trying to switch activity
                if (viewModel.loginStatus.value == LoginStatus.Success) {
                    Log.d("DEV-AHGAH", "Entering this if")
                    navController.navigate(NavigationRoute.MenuScreen.route)
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
                    context,
                    "Login succeeded",
                    Toast.LENGTH_SHORT
                ).show()

                LoginStatus.Fail -> Toast.makeText(
                    context,
                    "Login failed",
                    Toast.LENGTH_SHORT
                ).show()

                LoginStatus.Unknown -> {}
            }
        }
    }
}