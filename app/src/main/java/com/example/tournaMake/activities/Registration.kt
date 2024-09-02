package com.example.tournaMake.activities

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.tournaMake.activities.navgraph.NavigationRoute
import com.example.tournaMake.data.models.AuthenticationViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.MainProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
fun handleRegistration(
    username: String,
    password: String,
    email: String,
    rememberMe: Boolean,
    viewModel: AuthenticationViewModel,
    owner: LifecycleOwner,
    navController: NavController
) {
    val appDatabase = inject<AppDatabase>(AppDatabase::class.java)
    val mainProfile = MainProfile(username, password, email, "", 0, 0.0, 0.0)
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            appDatabase.value.mainProfileDao().insert(mainProfile)
            //val intent = Intent(this, RegistrationPhotoActivity::class.java)
            //startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    if (rememberMe) {
        viewModel.saveUserAuthenticationPreferences(email, password, true)
    }
    navController.navigate(NavigationRoute.MenuScreen.route)
}