package com.example.tournaMake.activities

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.tournaMake.activities.navgraph.NavigationRoute
import com.example.tournaMake.data.models.AuthenticationViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.MainProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

fun handleRegistration(
    username: String,
    password: String,
    email: String,
    rememberMe: Boolean,
    viewModel: AuthenticationViewModel,
    owner: LifecycleOwner,
    navController: NavController,
    context: Context
) {
    val appDatabase = inject<AppDatabase>(AppDatabase::class.java)
    val mainProfile = MainProfile(username, password, email, "", 0, 0.0, 0.0)

    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            if (appDatabase.value.mainProfileDao().checkEmail(email) == 1) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Email already exists", Toast.LENGTH_SHORT).show()
                }
            } else {
                appDatabase.value.mainProfileDao().insert(mainProfile)
                if (rememberMe) {
                    viewModel.saveUserAuthenticationPreferences(email, password, true)
                } else {
                    viewModel.saveUserAuthenticationPreferences(email, password, false)
                }
                withContext(Dispatchers.Main){
                    navController.navigate(NavigationRoute.RegistrationPhotoScreen.route)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}