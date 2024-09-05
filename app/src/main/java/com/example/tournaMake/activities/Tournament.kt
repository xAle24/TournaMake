package com.example.tournaMake.activities

import android.view.Window
import androidx.core.view.WindowCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.tournaMake.data.models.TournamentDataViewModel
import com.example.tournaMake.mylibrary.displaymodels.BracketDisplayModel
import com.example.tournaMake.mylibrary.displaymodels.BracketMatchDisplayModel
import com.example.tournaMake.mylibrary.displaymodels.BracketRoundDisplayModel
import com.example.tournaMake.mylibrary.displaymodels.BracketTeamDisplayModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.TournamentMatchData
import com.example.tournaMake.tournamentmanager.TournamentManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
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

data class TournamentManagerUpdateRequest(
    val firstTeamName: String,
    val secondTeamName: String,
    val isFirstTeamWinner: Boolean,
    val isSecondTeamWinner: Boolean,
    val firstTeamScore: Int,
    val secondTeamScore: Int
)
fun fetchStuffForTournament(
    tournamentID: String,
    tournamentDataViewModel: TournamentDataViewModel,
    owner: LifecycleOwner
) {
    val appDatabase by inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            val tournamentMatchesAndTeamsData =
                appDatabase.tournamentDao().getMatchesAndTeamsFromTournamentID(tournamentID)
            val matchesInTournament = appDatabase.matchDao().getMatchesInTournament(tournamentID)
            val tournament = appDatabase.tournamentDao().getTournamentFromID(tournamentID)
            tournamentDataViewModel.changeMatchesList(tournamentMatchesAndTeamsData)
            tournamentDataViewModel.changeDbMatches(matchesInTournament)
            tournamentDataViewModel.changeTournamentName(tournament.name)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun createBracket(
    tournament: TournamentDataViewModel,
    tournamentManager: TournamentManager,
) {
    val listOfTournamentData = tournament.tournamentMatchesAndTeamsLiveData.value ?: emptyList()
    val numberOfRounds = ceil(log2(listOfTournamentData.size.toDouble())).toInt()
    val bracketDisplayModel: BracketDisplayModel?
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
                                isWinner = listOfTournamentData[index].isWinner == 1,
                                score = listOfTournamentData[index].score.toString()
                            ),
                            BracketTeamDisplayModel(
                                name = listOfTournamentData[index + 1].name,
                                isWinner = listOfTournamentData[index + 1].isWinner == 1,
                                score = listOfTournamentData[index + 1].score.toString()
                            )
                        ) to listOfTournamentData[index]
                    }
                    .map {
                        /**
                         * Pairs of:
                         *    Pair<BracketTeamDisplayModel, BracketTeamDisplayModel>
                         *        and
                         *    TournamentMatchData
                         * */
                        BracketMatchDisplayModel(it.first.first, it.first.second, it.second)
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
        if (!tournamentManager.wasBracketInitialised()) {
            tournamentManager.setTournamentMatchData(listOfTournamentData)
            tournamentManager.initMap()
            tournamentManager.setBracket(bracketDisplayModel)
        }
    }
}

fun createRoundWithPlaceholders(
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
                BracketMatchDisplayModel(it.first, it.second, null)
            }
            .toList()
    )
}

fun getMatchesNamesAsCompetingTeams(data: List<TournamentMatchData>): List<MatchAsCompetingTeams> {
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
fun getPairOfTeamIDAndNameFromMatchID(
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

fun updateMatch(
    tournamentDataViewModel: TournamentDataViewModel,
    data: DatabaseMatchUpdateRequest,
    tournamentID: String,
    owner: LifecycleOwner
) {
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            /*appDatabase.teamDao().updateTeam(
                teamID = data.firstTeamID,
                isWinner = if (data.isFirstTeamWinner) 'Y' else 'N',
                score = data.firstTeamScore
            )
            appDatabase.teamDao().updateTeam(
                teamID = data.secondTeamID,
                isWinner = if (data.isSecondTeamWinner) 'Y' else 'N',
                score = data.secondTeamScore
            )*/

            // Causes a recomposition to happen
            fetchStuffForTournament(
                tournamentID = tournamentID,
                tournamentDataViewModel = tournamentDataViewModel,
                owner
            )

            if (data.isFirstTeamWinner) {
                tournamentDataViewModel.tournamentMatchesAndTeamsLiveData.value?.indexOf(
                    tournamentDataViewModel.tournamentMatchesAndTeamsLiveData.value!!
                        .first { t -> t.teamID == data.firstTeamID })
            } else if (data.isSecondTeamWinner) {
                tournamentDataViewModel.tournamentMatchesAndTeamsLiveData.value?.indexOf(
                    tournamentDataViewModel.tournamentMatchesAndTeamsLiveData.value!!
                        .first { t -> t.teamID == data.secondTeamID })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * Configures our [MainActivity] window so that it reaches edge to edge of the device, meaning
 * content can be rendered underneath the status and navigation bars.
 *
 * This method works hand in hand with [com.example.tournaMake.ui.screens.tournament.ConfigureTransparentSystemBars],
 * to make sure content behind these bars is visible.
 *
 * Keep in mind that if you need to make sure your content padding doesn't clash with the status bar text/icons,
 * you can leverage modifiers like `windowInsetsPadding()` and `systemBarsPadding()`. For more information,
 * read the Compose WindowInsets docs:
 * https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/WindowInsets
 */
fun configureEdgeToEdgeWindow(window: Window) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
}