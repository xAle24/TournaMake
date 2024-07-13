package com.example.tournaMake.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.GamesListViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.Game
import com.example.tournaMake.ui.screens.menu.GamesListScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel

class GamesListActivity : ComponentActivity() {
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
            // View Model of profile list
            val gamesListViewModel = koinViewModel<GamesListViewModel>()
            fetchAndUpdateGameList(gamesListViewModel)

            GamesListScreen(
                state = state.value,
                gamesListViewModel.gamesListLiveData,
                addGame = this::addGame,
                backButton = this::backButton,
                recreationFunction = this::recreate
            )
        }
    }
    private fun fetchAndUpdateGameList(gamesListViewModel: GamesListViewModel) {
        var gamesList: List<Game>
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                gamesList = appDatabase?.gameDao()?.getAll() ?: emptyList()
                gamesListViewModel.changeGameList(gamesList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun addGame(game: Game) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                appDatabase?.gameDao()?.insertAll(game)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun backButton() {
        finish()
    }
}