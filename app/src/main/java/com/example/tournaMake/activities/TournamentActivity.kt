package com.example.tournaMake.activities

//import com.example.tournaMake.mylibrary.ui.SingleEliminationBracket
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.view.WindowCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.data.models.TournamentDataViewModel
import com.example.tournaMake.data.models.TournamentIDViewModel
import com.example.tournaMake.mylibrary.displaymodels.BracketDisplayModel
import com.example.tournaMake.mylibrary.displaymodels.BracketMatchDisplayModel
import com.example.tournaMake.mylibrary.displaymodels.BracketRoundDisplayModel
import com.example.tournaMake.mylibrary.displaymodels.BracketTeamDisplayModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.TournamentMatchData
import com.example.tournaMake.ui.screens.tournament.TournamentScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel

class TournamentActivity : ComponentActivity() {
    private val appDatabase = get<AppDatabase>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureEdgeToEdgeWindow()

        setContent {
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val state = themeViewModel.state.collectAsStateWithLifecycle()
            val tournamentIDViewModel = koinViewModel<TournamentIDViewModel>()
            val tournamentID = tournamentIDViewModel.tournamentID.collectAsStateWithLifecycle()
            val tournamentDataViewModel = koinViewModel<TournamentDataViewModel>()
            val liveDataObserver = Observer<List<TournamentMatchData>> {
                Log.d("DEV", "In TournamentActivity: Observer code called! List size = ${it.size}")
                // When data arrives, create the bracket
                //bracket = createBracket(tournamentDataViewModel)
            }
            tournamentDataViewModel.tournamentMatchesAndTeamsLiveData.observe(this, liveDataObserver)
            val tournamentLiveData = tournamentDataViewModel.tournamentMatchesAndTeamsLiveData.observeAsState(
                emptyList()
            )
            fetchStuffForTournament(tournamentID.value, tournamentDataViewModel)
            if (tournamentLiveData.value.isNotEmpty()) {
                TournamentScreen(
                    state = state.value,
                    bracket = createBracket(tournamentDataViewModel)
                )
            }
            //SingleEliminationBracket(bracket = TestTournamentData.singleEliminationBracket)
        }
    }

    private fun fetchStuffForTournament(
        tournamentID: String,
        tournamentDataViewModel: TournamentDataViewModel
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val tournamentMatchesAndTeamsData =
                    appDatabase.tournamentDao().getMatchesAndTeamsFromTournamentID(tournamentID)
                tournamentDataViewModel.changeMatchesList(tournamentMatchesAndTeamsData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 1) bisogna aspettare che i live data arrivino
     * 2) bisogna creare il bracket
     * 3) bisogna creare la schermata
     * */

    /**
     * Input: tournament data, che contiene team e match
     * Bracket contiene più round
     * I round (es. semifinali, finali) contengono team / 2 matches
     * un match è giocato da 2 team
     * */
    private fun createBracket(tournament: TournamentDataViewModel): BracketDisplayModel {
        val listOfTournamentData = tournament.tournamentMatchesAndTeamsLiveData.value ?: emptyList()
        return if (listOfTournamentData.isNotEmpty()) BracketDisplayModel(
            name = "MyBracket",
            rounds = listOf(
                BracketRoundDisplayModel(
                    name = "Round 0",
                    matches = generateSequence(0) { it + 2 }
                        .take(listOfTournamentData.size / 2)
                        .map { index ->
                            Pair(
                                BracketTeamDisplayModel(
                                    name = listOfTournamentData[index].name,
                                    isWinner = false,
                                    score = "0"
                                ),
                                BracketTeamDisplayModel(
                                    name = listOfTournamentData[index + 1].name,
                                    isWinner = false,
                                    score = "0"
                                )
                            )
                        }
                        .map {
                            BracketMatchDisplayModel(it.first, it.second)
                        }
                        .toList()
                )
            )
        ) else BracketDisplayModel("EmptyBracket", emptyList())
    }

    /**
     * Configures our [MainActivity] window so that it reaches edge to edge of the device, meaning
     * content can be rendered underneath the status and navigation bars.
     *
     * This method works hand in hand with [ConfigureTransparentSystemBars], to make sure content
     * behind these bars is visible.
     *
     * Keep in mind that if you need to make sure your content padding doesn't clash with the status bar text/icons,
     * you can leverage modifiers like `windowInsetsPadding()` and `systemBarsPadding()`. For more information,
     * read the Compose WindowInsets docs: https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/WindowInsets
     */
    private fun configureEdgeToEdgeWindow() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}