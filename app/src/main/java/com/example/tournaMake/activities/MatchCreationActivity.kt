package com.example.tournaMake.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.MatchCreationViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.ui.screens.match.MatchCreationScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

class MatchCreationActivity : ComponentActivity() {
    private val appDatabase = inject<AppDatabase>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val state = themeViewModel.state.collectAsStateWithLifecycle()
            val matchCreationViewModel = koinViewModel<MatchCreationViewModel>()
            fetchData(matchCreationViewModel)
            MatchCreationScreen(
                state = state.value,
                backFunction = this::goBack,
                navigateToMatch = this::navigateToMatch,
                gamesListLiveData = matchCreationViewModel.games
            )
        }
    }

    private fun fetchData(vm: MatchCreationViewModel) {
        lifecycleScope.launch (Dispatchers.IO) {
            try {
                val games = appDatabase.value.gameDao().getAll()
                val mainProfiles = appDatabase.value.mainProfileDao().getAll()
                val guestProfiles = appDatabase.value.guestProfileDao().getAll()
                vm.changeGamesList(games)
                vm.changeMainProfiles(mainProfiles)
                vm.changeGuestProfiles(guestProfiles)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun navigateToMatch() {
        val intent = Intent(this, MatchActivity::class.java)
        startActivity(intent)
    }
    private fun goBack() {
        finish()
    }
}