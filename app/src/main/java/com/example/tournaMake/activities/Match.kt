package com.example.tournaMake.activities

import androidx.activity.compose.BackHandler
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.navOptions
import com.example.tournaMake.activities.navgraph.NavigationRoute
import com.example.tournaMake.data.constants.MatchResult
import com.example.tournaMake.data.constants.mapMatchResultToInteger
import com.example.tournaMake.data.models.MatchViewModel
import com.example.tournaMake.data.models.TeamDataPacket
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.ui.screens.match.TeamUIImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

fun fetchMatchData(matchViewModel: MatchViewModel, owner: LifecycleOwner) {
    val appDatabase = inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch(Dispatchers.IO) {
        // The call to collect ensures the data are fetched from the data store
        matchViewModel.selectedMatchId.collect {
            if (it != null) {
                val playedMatch = appDatabase.value.matchDao().getMatchFromID(it)
                val playedGame = appDatabase.value.gameDao().getGameFromID(playedMatch.gameID)
                val teamsInTm =
                    appDatabase.value.teamInTmDao().getTeamsInTmFromMatch(playedMatch.matchTmID)
                val teams = appDatabase.value.teamDao().getAll()
                    .filter { team ->
                        teamsInTm
                            .map { teamInTm -> teamInTm.teamID }.contains(team.teamID)
                    }
                val teamDataPackets = teams.map { team ->
                    val mainParticipants = appDatabase.value.mainParticipantsDao()
                        .getAllMainParticipantsFromTeam(team.teamID)
                    val guestParticipants = appDatabase.value.guestParticipantsDao()
                        .getAllGuestParticipantsFromTeam(team.teamID)
                    val mainProfiles = mainParticipants.map { mainParticipant ->
                        appDatabase.value.mainProfileDao()
                            .getProfileByEmail(mainParticipant.email)
                    }
                    val guestProfiles = guestParticipants.map { guestParticipant ->
                        appDatabase.value.guestProfileDao()
                            .getFromUsername(guestParticipant.username)
                    }
                    val teamInTM =
                        teamsInTm.first { teamInTm -> teamInTm.teamID == team.teamID }
                    val teamScore = teamInTM.score
                    return@map TeamDataPacket(
                        teamUI = TeamUIImpl(
                            mainProfiles.toSet(),
                            guestProfiles.toSet(),
                            team.name
                        ),
                        teamScore = teamScore,
                        teamID = team.teamID,
                        isWinner = teamInTM.isWinner == 1
                    )
                }
                matchViewModel.changeTeamDataPackets(teamDataPackets)
                matchViewModel.changeTeamUIs(teamDataPackets.map { packet -> packet.teamUI })
                matchViewModel.changePlayedGame(playedGame)
                matchViewModel.changeTeams(teams)
                matchViewModel.changeTeamsInMatch(teamsInTm)
                matchViewModel.changeMatch(playedMatch)
            }
        }
    }
}

/**
 * Saves the current scores of each team.
 * The string is the TeamID. The integer is the score, and the match result
 * is an enum representing if the team won, lost or obtained a draw.
 * */
fun saveMatch(
    teamScores: Map<String, Pair<Int, MatchResult>>,
    matchID: String,
    owner: LifecycleOwner
) {
    val appDatabase = inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch(Dispatchers.IO) {
        val newTeamInTms = teamScores
            .map {
                val teamInTM = appDatabase.value.teamInTmDao().findByID(it.key, matchID)
                teamInTM.score = it.value.first
                return@map teamInTM
            }
        appDatabase.value.teamInTmDao().updateTeamInTms(newTeamInTms)
    }
}

fun endMatch(
    navController: NavController,
    navigationRoute: String, // may vary from MatchListScreen to TournamentScreen
    teamScores: Map<String, Pair<Int, MatchResult>>,
    matchID: String,
    owner: LifecycleOwner
) {
    val appDatabase = inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch(Dispatchers.IO) {
        val newTeamInTms = teamScores
            .map {
                val teamInTM = appDatabase.value.teamInTmDao().findByID(it.key, matchID)
                teamInTM.score = it.value.first
                teamInTM.isWinner = mapMatchResultToInteger(it.value.second)
                return@map teamInTM
            }
        appDatabase.value.teamInTmDao().updateTeamInTms(newTeamInTms)
        // Ending the match
        appDatabase.value.matchDao().endMatch(matchID)
        withContext(Dispatchers.Main) {
            val navBackStackEntry = navController.previousBackStackEntry
            // If we got to this screen from the match creation, we need to go back twice
            // to skip that screen and go back to the match list

            when (navBackStackEntry?.arguments?.getString("source")) {
                "tournament" -> {
                    // If the user came from the tournament, pop back to refresh tournament screen
                    navController.popBackStack(NavigationRoute.TournamentScreen.route, false)
                    // You can also pass back arguments via savedStateHandle to refresh UI
                    //navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true)
                }

                "creation" -> {
                    // If the user came from match creation, pop directly to the matches list
                    navController.popBackStack(NavigationRoute.MatchesListScreen.route, false)
                }

                else -> {
                    // Default behavior if coming from the matches list or another source
                    navController.popBackStack()
                }
            }

        }
    }
}