package com.example.tournaMake.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.GraphViewModel
import com.example.tournaMake.data.models.AuthenticationViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.Match
import com.example.tournaMake.sampledata.PlayedGame
import com.example.tournaMake.ui.screens.profile.ChartScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel

class GamesChartActivity : ComponentActivity(){
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
            val graphViewModel = koinViewModel<GraphViewModel>()
            val gameObserver = Observer<List<PlayedGame>?> { game ->
                Log.d("DEV", "In game observer profile = $game")//TODO remove
            }
            val matchObserver = Observer<List<Match>?> { match ->
                Log.d("DEV", "In match observer profile = $match")//TODO remove
            }
            graphViewModel.gamesListLiveData.observe(this, gameObserver)
            graphViewModel.matchListLiveData.observe(this, matchObserver)

            // Maybe this val needs to be created here to trigger the recomposition of the whole activity
            val gamesList by graphViewModel.gamesListLiveData.observeAsState()

            fetchAndUpdateGraph(loggedEmail.value.loggedProfileEmail, graphViewModel)
            ChartScreen(
                state = state.value,
                gamesLiveData = graphViewModel.gamesListLiveData,
                backButton = this::backButton
            )
        }
    }
    private fun fetchAndUpdateGraph(email: String, graphViewModel: GraphViewModel) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val myGames = appDatabase?.gameDao()?.getPlayedGames(email) ?: emptyList()
                Log.d("DEV", "In GamesChartActivity.kt, myGames = $myGames")
                graphViewModel.changeGamesList(myGames)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun backButton() {
        finish()
    }
}