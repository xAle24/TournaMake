package com.example.tournaMake.activities

//import com.example.tournaMake.mylibrary.ui.SingleEliminationBracket
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.key
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
import com.example.tournaMake.tournamentmanager.TournamentManager
import com.example.tournaMake.ui.screens.tournament.TournamentScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import kotlin.math.ceil
import kotlin.math.log2

data class MatchAsCompetingTeams(
    val matchID: String, // needed for database
    /* Data the user needs to see */
    val firstTeamName: String,
    val firstTeamID: String,
    val secondTeamName: String,
    val secondTeamID: String
)

data class DatabaseMatchUpdateRequest(
    val matchID: String,
    val firstTeamID: String,
    val secondTeamID: String,
    val isFirstTeamWinner: Boolean,
    val isSecondTeamWinner: Boolean,
    val firstTeamScore: Int,
    val secondTeamScore: Int
)

class TournamentActivity : ComponentActivity() {
    private val appDatabase = get<AppDatabase>()
    private lateinit var tournamentManager: TournamentManager
    private lateinit var bracket: BracketDisplayModel
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
            tournamentDataViewModel.tournamentMatchesAndTeamsLiveData.observe(
                this,
                liveDataObserver
            )
            val tournamentLiveData =
                tournamentDataViewModel.tournamentMatchesAndTeamsLiveData.observeAsState(
                    emptyList()
                )
            fetchStuffForTournament(tournamentID.value, tournamentDataViewModel)
            if (tournamentLiveData.value.isNotEmpty()) {
                key(tournamentLiveData.value) {
                    Log.d("DEV", "Key() function at line 77 in TournamentActivity called")
                    TournamentScreen(
                        state = state.value,
                        bracket = this.bracket,
                        matchesAndTeams = getMatchesNamesAsCompetingTeams(tournamentLiveData.value),
                        onConfirmCallback = {
                            updateMatch(
                                tournamentDataViewModel = tournamentDataViewModel,
                                data = it,
                                tournamentID.value
                            )
                        }
                    )
                }
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
        val numberOfRounds = ceil(log2(listOfTournamentData.size.toDouble())).toInt()
        var bracketDisplayModel: BracketDisplayModel? = null
        if (listOfTournamentData.isNotEmpty()) {
            val roundList = mutableListOf(
                BracketRoundDisplayModel(
                    name = "Round 0",
                    matches = generateSequence(0) { it + 2 }
                        .take(listOfTournamentData.size / 2)
                        .map { index ->
                            Pair(
                                BracketTeamDisplayModel(
                                    name = listOfTournamentData[index].name,
                                    isWinner = listOfTournamentData[index].isWinner == 'Y',
                                    score = listOfTournamentData[index].score.toString()
                                ),
                                BracketTeamDisplayModel(
                                    name = listOfTournamentData[index + 1].name,
                                    isWinner = listOfTournamentData[index + 1].isWinner == 'Y',
                                    score = listOfTournamentData[index + 1].score.toString()
                                )
                            )
                        }
                        .map {
                            BracketMatchDisplayModel(it.first, it.second)
                        }
                        .toList()
                )
            )
            val halfOfTheTeams = listOfTournamentData.size / 2
            // Add the placeholder rounds
            for (i in 1 until numberOfRounds) {
                val numberOfMatches = halfOfTheTeams / (i * 2)
                roundList.add(createRoundWithPlaceholders(numberOfMatches, i))
            }

            bracketDisplayModel = BracketDisplayModel("MyBracket", roundList)
            this.tournamentManager = TournamentManager(
                bracket = bracketDisplayModel,
                tournamentData = listOfTournamentData
            )
            this.tournamentManager.init()
            this.bracket = bracketDisplayModel
        }
        return bracketDisplayModel ?: BracketDisplayModel("EmptyBracket", emptyList())
    }

    private fun createRoundWithPlaceholders(
        matchesNumber: Int,
        roundIndex: Int
    ): BracketRoundDisplayModel {
        return BracketRoundDisplayModel(
            name = "Round $roundIndex",
            matches = generateSequence(0) { it + 1 }
                .take(matchesNumber)
                .map {
                    Pair(
                        BracketTeamDisplayModel(
                            name = "---",
                            isWinner = false,
                            score = "0"
                        ),
                        BracketTeamDisplayModel(
                            name = "---",
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
    }

    private fun getMatchesNamesAsCompetingTeams(data: List<TournamentMatchData>): List<MatchAsCompetingTeams> {
        val matchesList = data.map { it.matchTmID }.distinct().toList()
        return matchesList
            .map { matchID -> matchID to getPairOfTeamIDAndNameFromMatchID(matchID, data) }
            .map { strangePair ->
                MatchAsCompetingTeams(
                    matchID = strangePair.first,
                    firstTeamName = strangePair.second["First"]!!.second,
                    secondTeamName = strangePair.second["Second"]!!.second,
                    firstTeamID = strangePair.second["First"]!!.first,
                    secondTeamID = strangePair.second["Second"]!!.first
                )
            }
    }

    /**
     * Returns a map of:
     * First -> Pair(TeamID, TeamName)
     * Second -> Pair(TeamID, TeamName)
     * */
    private fun getPairOfTeamIDAndNameFromMatchID(
        matchID: String,
        data: List<TournamentMatchData>
    ): Map<String, Pair<String, String>> {
        val filteredData = data.filter { it.matchTmID == matchID }.toList()
        assert(filteredData.size == 2)
        return mapOf(
            "First" to Pair(filteredData[0].teamID, filteredData[0].name),
            "Second" to Pair(filteredData[1].teamID, filteredData[1].name)
        )
    }

    private fun updateMatch(
        tournamentDataViewModel: TournamentDataViewModel,
        data: DatabaseMatchUpdateRequest,
        tournamentID: String
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                appDatabase.teamDao().updateTeam(
                    teamID = data.firstTeamID,
                    isWinner = if (data.isFirstTeamWinner) 'Y' else 'N',
                    score = data.firstTeamScore
                )
                appDatabase.teamDao().updateTeam(
                    teamID = data.secondTeamID,
                    isWinner = if (data.isSecondTeamWinner) 'Y' else 'N',
                    score = data.secondTeamScore
                )

                // Causes a recomposition to happen
                fetchStuffForTournament(
                    tournamentID = tournamentID,
                    tournamentDataViewModel = tournamentDataViewModel
                )

                if (data.isFirstTeamWinner) {
                    tournamentDataViewModel.tournamentMatchesAndTeamsLiveData.value?.indexOf(
                        tournamentDataViewModel.tournamentMatchesAndTeamsLiveData.value!!.first { t -> t.teamID == data.firstTeamID })
                } else if (data.isSecondTeamWinner) {
                    tournamentDataViewModel.tournamentMatchesAndTeamsLiveData.value?.indexOf(
                        tournamentDataViewModel.tournamentMatchesAndTeamsLiveData.value!!.first { t -> t.teamID == data.secondTeamID })
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun teamWon(teamName: String, tournamentDataViewModel: TournamentDataViewModel) {
        val newTeamIndex = this.tournamentManager.mapTeamNameToIndex(teamName) / 2
        val matchIndex = newTeamIndex / 2
        val teamCurrentRound = this.tournamentManager.getTeamRound(teamName)
        this.tournamentManager.setTeamRound(teamName, teamCurrentRound + 1)
        val matchToUpdate = tournamentManager.getBracket().rounds[teamCurrentRound + 1].matches[matchIndex]
        if (newTeamIndex % 2 == 0) {
            // if the index is even, it means the team has to be inserted in the top team
            matchToUpdate.topTeam.name = teamName
            matchToUpdate.topTeam.isWinner = false
            matchToUpdate.topTeam.score = "0"
        } else {
            matchToUpdate.bottomTeam.name = teamName
            matchToUpdate.bottomTeam.isWinner = false
            matchToUpdate.bottomTeam.score = "0"
        }
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