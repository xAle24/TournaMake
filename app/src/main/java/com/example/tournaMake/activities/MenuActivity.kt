package com.example.tournaMake.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.AuthenticationViewModel
import com.example.tournaMake.data.models.NotificationViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.Notification
import com.example.tournaMake.ui.screens.menu.MenuScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel

class MenuActivity : ComponentActivity() {
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
            val authenticationViewModel = koinViewModel<AuthenticationViewModel>()
            val loggedEmail = authenticationViewModel.loggedEmail.collectAsStateWithLifecycle()
            val notificationViewModel = koinViewModel<NotificationViewModel>()
            fetchAndUpdateNotification(notificationViewModel, loggedEmail.value.loggedProfileEmail)
            MenuScreen(
                state = state.value,
                navigateToTournament= this::navigateToTournament,
                navigateToListProfile = this::navigateToListProfile,
                navigateToSettings = this::navigateToSettings,
                navigateToGamesList = this::navigateToGamesList,
                navigateToMatchesList = this::navigateToMatchesList,
                logout = this::logout,
                notificationLiveData = notificationViewModel.notificationLiveData
            )
        }
    }
    private fun fetchAndUpdateNotification(notificationViewModel: NotificationViewModel, email: String) {
        var notificationList: List<Notification>
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                notificationList = appDatabase?.notificationDao()?.getNotificationsByEmail(email) ?: emptyList()
                notificationViewModel.changeNotificationList(notificationList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun navigateToTournament() {
        val intent = Intent(this, TournamentListActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToGamesList() {
        val intent = Intent(this, GamesListActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToListProfile() {
        val intent = Intent(this, ProfileListActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToMatchesList() {
        val intent = Intent(this, MatchListActivity::class.java)
        startActivity(intent)
    }
    private fun logout() {
        val intent = Intent(
            this,
            MainActivity::class.java
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}