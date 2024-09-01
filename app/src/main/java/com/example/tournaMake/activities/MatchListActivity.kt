package com.example.tournaMake.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.MatchListViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.ui.screens.match.MatchListScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

class MatchListActivity : ComponentActivity() {
    private var appDatabase: AppDatabase? = get<AppDatabase>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val database = koinInject<AppDatabase>()
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val state = themeViewModel.state.collectAsStateWithLifecycle()
            val matchListViewModel = koinViewModel<MatchListViewModel>()
            fetchAndUpdateMatches(matchListViewModel, database)
            MatchListScreen(
                state = state.value,
                matchesListLiveData = matchListViewModel.matchesListLiveData,
                navigationFunction = this::navigateToMatchCreation,
                addFavoritesFunction = this::addMatchToFavorites,
                removeFavoritesFunction = this::removeMatchToFavorites,
                backFunction = this::goBack
            )
        }
    }

    private fun fetchAndUpdateMatches(vm: MatchListViewModel, db: AppDatabase) {
        lifecycleScope.launch (Dispatchers.IO) {
            try {
                val matches = db.matchDao().getAllWithGamesNames()
                vm.changeMatchesList(matches)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun addMatchToFavorites(matchTmID: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                appDatabase?.matchDao()?.setMatchFavorites(matchTmID)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun removeMatchToFavorites(matchTmID: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                appDatabase?.matchDao()?.removeMatchFavorites(matchTmID)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun navigateToMatchCreation() {
        val intent = Intent(this, MatchCreationActivity::class.java)
        startActivity(intent)
    }
    private fun goBack() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }
}