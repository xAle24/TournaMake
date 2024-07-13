package com.example.tournaMake.activities

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.GraphViewModel
import com.example.tournaMake.data.models.LoggedProfileViewModel
import com.example.tournaMake.data.models.ProfileViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.Game
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.sampledata.Match
import com.example.tournaMake.sampledata.PlayedGame
import com.example.tournaMake.ui.screens.profile.ChartScreen
import com.example.tournaMake.ui.screens.profile.ProfileScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel

class GamesChartActivity : ComponentActivity(){
    private var appDatabase: AppDatabase? = get<AppDatabase>()

    @RequiresApi(Build.VERSION_CODES.O)
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
            val loggedProfileViewModel = koinViewModel<LoggedProfileViewModel>()
            val loggedEmail = loggedProfileViewModel.loggedEmail.collectAsStateWithLifecycle()
            val graphViewModel = koinViewModel<GraphViewModel>()
            val gameObserver = Observer<List<PlayedGame>?> { game ->
                Log.d("DEV", "In profile observer profile = $game")//TODO remove
            }
            val matchObserver = Observer<List<Match>?> { match ->
                Log.d("DEV", "In profile observer profile = $match")//TODO remove
            }
            graphViewModel.gamesListLiveData.observe(this, gameObserver)
            graphViewModel.matchListLiveData.observe(this, matchObserver)
            fetchAndUpdateGraph(loggedEmail.value.loggedProfileEmail, graphViewModel)
            //val profile
            ChartScreen(
                state = state.value,
                /*
                * TODO: consider passing the Observer as a parameter instead of the MainProfile
                *  (forse è una cattiva idea, ma magari si può avere il codice dell'observer
                *  sott'occhio al momento di costruire il ProfileScreen).
                * */
                //profileViewModel.profileLiveData.value
                gamesLiveData = graphViewModel.gamesListLiveData,
                backButton = this::backButton
            )
        }
    }
    private fun fetchAndUpdateGraph(email: String, graphViewModel: GraphViewModel) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val myGames = appDatabase?.gameDao()?.getPlayedGames(email) ?: emptyList()
                Log.d("DEV", "In getProfile() coroutine, myProfile.email = $myGames")
                // Now update the data in the view model, to trigger the onchange method of the attached
                // observer
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