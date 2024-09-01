package com.example.tournaMake.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.AuthenticationViewModel
import com.example.tournaMake.data.models.MatchListViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.MatchGameData
import com.example.tournaMake.ui.screens.profile.MatchListScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel

//last played match
class PlayerActActivity : ComponentActivity(){
    private var appDatabase: AppDatabase? = get<AppDatabase>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val state = themeViewModel.state.collectAsStateWithLifecycle()
            val authenticationViewModel = koinViewModel<AuthenticationViewModel>()
            val loggedEmail = authenticationViewModel.loggedEmail.collectAsStateWithLifecycle()
            val matchListViewModel = koinViewModel<MatchListViewModel>()
            val matchObserver = Observer<List<MatchGameData>?> { match ->
                Log.d("DEV", "In profile observer profile = $match")
                // TODO: add rest of the profile code
            }
            matchListViewModel.matchesListLiveData.observe(this, matchObserver)
            fetchAndUpdateMatch(loggedEmail.value.loggedProfileEmail, matchListViewModel)
            MatchListScreen(
                state = state.value,
                matchListLiveData = matchListViewModel.matchesListLiveData,
                backButton = this::backButton
            )
        }
    }
    private fun fetchAndUpdateMatch(email: String, matchListViewModel: MatchListViewModel) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val myMatch = appDatabase?.matchDao()?.getMyMatches(email) ?: emptyList()
                Log.d("DEV", "In getProfile() coroutine, myProfile.email = $myMatch")
                // Now update the data in the view model, to trigger the onchange method of the attached
                // observer
                matchListViewModel.changeMatchesList(myMatch)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun backButton() {
        finish()
    }
}