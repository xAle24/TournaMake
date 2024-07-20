package com.example.tournaMake.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.data.models.TournamentListViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.ui.screens.tournament.TournamentListScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

class TournamentListActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val database = koinInject<AppDatabase>()
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val state = themeViewModel.state.collectAsStateWithLifecycle()
            val tournamentListViewModel = koinViewModel<TournamentListViewModel>()
            fetchAndUpdateTournament(tournamentListViewModel, database)
            TournamentListScreen(
                state = state.value,
                matchesListLiveData = tournamentListViewModel.tournamentListLiveData,
                navigateToTournamentCreation = this::navigateToTournamentCreation,
                backFunction = this::finish
            )
        }
    }

    private fun fetchAndUpdateTournament(vm: TournamentListViewModel, db: AppDatabase) {
        lifecycleScope.launch (Dispatchers.IO) {
            try {
                val tournaments = db.tournamentDao().getAll()
                vm.changeTournamentList(tournaments)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun navigateToTournamentCreation() {
        val intent = Intent(this, TournamentCreationActivity::class.java)
        startActivity(intent)
    }
}