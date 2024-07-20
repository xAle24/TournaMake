package com.example.tournaMake.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.data.models.TournamentCreationViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.Game
import com.example.tournaMake.sampledata.GuestProfile
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.sampledata.TournamentType
import com.example.tournaMake.ui.screens.tournament.TournamentCreationScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel

class TournamentCreationActivity : ComponentActivity() {
    private var appDatabase: AppDatabase? = get<AppDatabase>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val state = themeViewModel.state.collectAsStateWithLifecycle()
            val tournamentCreationViewModel = koinViewModel<TournamentCreationViewModel>()
            fetchAndUpdateGamesList(tournamentCreationViewModel)
            fetchAndUpdateTournamentTypeList(tournamentCreationViewModel)
            fetchAndUpdateGuestProfileList(tournamentCreationViewModel)
            fetchAndUpdateMainProfileList(tournamentCreationViewModel)
            TournamentCreationScreen(
                state = state.value,
                tournamentCreationViewModel.gamesListLiveData,
                tournamentCreationViewModel.tournamentListLiveData,
                tournamentCreationViewModel.mainProfileListLiveData,
                tournamentCreationViewModel.guestProfileListLiveData,
                navigateToTournament = this::navigateToTournament,
                backFunction = this::finish
            )
        }
    }

    private fun navigateToTournament() {
        val intent = Intent(this, TournamentActivity::class.java)
        startActivity(intent)
    }

    private fun fetchAndUpdateGamesList(tournamentCreationViewModel: TournamentCreationViewModel) {
        var gamesList: List<Game>
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                gamesList = appDatabase?.gameDao()?.getAll() ?: emptyList()
                tournamentCreationViewModel.changeGameList(gamesList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchAndUpdateTournamentTypeList(tournamentCreationViewModel: TournamentCreationViewModel) {
        var typeList: List<TournamentType>
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                typeList = appDatabase?.tournamentTypeDao()?.getAll() ?: emptyList()
                tournamentCreationViewModel.changeTournamentTypeList(typeList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchAndUpdateGuestProfileList(tournamentCreationViewModel: TournamentCreationViewModel) {
        var guestProfileList: List<GuestProfile>
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                guestProfileList = appDatabase?.guestProfileDao()?.getAll() ?: emptyList()
                tournamentCreationViewModel.changeGuestProfileList(guestProfileList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchAndUpdateMainProfileList(tournamentCreationViewModel: TournamentCreationViewModel) {
        var mainProfileList: List<MainProfile>
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                mainProfileList = appDatabase?.mainProfileDao()?.getAll() ?: emptyList()
                tournamentCreationViewModel.changeMainProfileList(mainProfileList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
