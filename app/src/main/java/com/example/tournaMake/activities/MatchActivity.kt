package com.example.tournaMake.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.tournaMake.activities.navgraph.NavigationRoute
import com.example.tournaMake.data.constants.MatchResult
import com.example.tournaMake.data.constants.mapMatchResultToInteger
import com.example.tournaMake.data.models.MatchViewModel
import com.example.tournaMake.data.models.TeamDataPacket
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.ui.screens.match.MatchScreen
import com.example.tournaMake.ui.screens.match.TeamUIImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import org.koin.java.KoinJavaComponent.inject

class MatchActivity : ComponentActivity() {
    private val appDatabase = inject<AppDatabase>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val state = themeViewModel.state.collectAsStateWithLifecycle()
            val matchViewModel = koinViewModel<MatchViewModel>()
            var wereFavoritesChangedFlag by remember { mutableStateOf(false) }
            fetchMatchData(matchViewModel)
            MatchScreen(
                state = state.value,
                gameImage = null,
                vm = matchViewModel,
                addMatchToFavorites = this::addMatchToFavorites,
                removeMatchFromFavorites = this::removeMatchToFavorites,
                backFunction = {
                    if (wereFavoritesChangedFlag) {
                        /* Informs the caller activity that it needs to recreate */
                        val data = Intent()
                        setResult(RESULT_OK, data)
                    }
                    val stringValue = intent.getStringExtra("CALLER_ACTIVITY")
                    if (stringValue == "MatchListActivity") {
                        /*val intent = Intent(this, MatchListActivity::class.java)
                        this@MatchActivity.navigateToActivity(intent)*/
                    }
                    finish()
                    /* TODO: this activity is a bit different. It should return a result, but
                    *   the intent should be modified. It needs to be the MatchListActivity (since
                    *   calling "finish()" now brings back to the match creation activity), or the
                    *   tournament activity. */
                },
                endMatch = this::endMatch,
                saveMatch = this::saveMatch,
                setFlag = { wereFavoritesChangedFlag = true }
            )
        }
    }

    private fun fetchMatchData(matchViewModel: MatchViewModel) {
        lifecycleScope.launch(Dispatchers.IO) {
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
                    matchViewModel.changeTeamUIs(teamDataPackets.map { it.teamUI })
                    matchViewModel.changePlayedGame(playedGame)
                    matchViewModel.changeTeams(teams)
                    matchViewModel.changeTeamsInMatch(teamsInTm)
                    matchViewModel.changeMatch(playedMatch)
                }
            }
        }
    }

    private fun addMatchToFavorites(matchTmID: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Log.d("DEV-MATCH-ACTIVITY", "Value of matchID: $matchTmID")
                appDatabase.value.matchDao().setMatchFavorites(matchTmID)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun removeMatchToFavorites(matchTmID: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                appDatabase.value.matchDao().removeMatchFavorites(matchTmID)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Saves the current scores of each team.
     * The string is the TeamID. The integer is the score, and the match result
     * is an enum representing if the team won, lost or obtained a draw.
     * */
    private fun saveMatch(teamScores: Map<String, Pair<Int, MatchResult>>, matchID: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val newTeamInTms = teamScores
                .map {
                    val teamInTM = appDatabase.value.teamInTmDao().findByID(it.key, matchID)
                    teamInTM.score = it.value.first
                    return@map teamInTM
                }
            appDatabase.value.teamInTmDao().updateTeamInTms(newTeamInTms)
        }
    }

    private fun endMatch(teamScores: Map<String, Pair<Int, MatchResult>>, matchID: String) {
        lifecycleScope.launch(Dispatchers.IO) {
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
            // Navigating back to matches list screen
            /*val intent = Intent(this@MatchActivity, MatchListActivity::class.java)
            startActivity(intent)*/
            finish() // instead of creating a new activity, we can just ensure this one ends and the caller activity is brought to the top
        }
    }

    private fun navigateToActivity(intent: Intent) {
        startActivity(intent)
    }
}

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
                matchViewModel.changeTeamUIs(teamDataPackets.map { it.teamUI })
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
fun saveMatch(teamScores: Map<String, Pair<Int, MatchResult>>, matchID: String, owner: LifecycleOwner) {
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

        navController.navigate(navigationRoute)
    }
}