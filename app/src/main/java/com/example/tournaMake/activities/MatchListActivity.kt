package com.example.tournaMake.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
    //TODO: SEE IF THIS WORKS
    /* This should allow the activity to recreate if the user sets other favourites in the
    * match details screen. */
    private val resultReceiver = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            recreate()
        }
    }
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
                backFunction = this::goBack,
                navigateToSpecifiedMatch = {matchID, isOver -> this.navigateToSpecifiedMatch(matchID, isOver, matchListViewModel)}
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

    private fun navigateToSpecifiedMatch(matchTmID: String, isOver: Boolean, vm: MatchListViewModel) {
        lifecycleScope.launch (Dispatchers.IO) {
            // Change selected match in repository
            vm.changeRepository(matchTmID)
            if (isOver) {
                val intent = Intent(this@MatchListActivity, MatchDetailsActivity::class.java)
                /*startActivity(intent)*/
                resultReceiver.launch(intent)
            } else {
                val intent = Intent(this@MatchListActivity, MatchActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun goBack() {
        finish()
    }
}