package com.example.tournaMake.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.MatchDetailsViewModel
import com.example.tournaMake.data.models.TeamDataPacket
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.ui.screens.match.MatchDetailsScreen
import com.example.tournaMake.ui.screens.match.TeamUIImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

/**
 * README: How to refresh data in the activity that launches another activity through an Intent
 *
 * The concrete case I was dealing with was having to refresh the favorites icons in the Match
 * List Activity, which is the one that launches this activity (further on it will be joined
 * by the tournament activities). If I set the favorite field of a match in this activity,
 * the match list activity would only show that change after recreation.
 *
 * To allow recreation upon calling finish in this "child" activity, we need to do some preparation:
 *
 * - FORMER ACTIVITY (in this case, MatchListActivity)
 * Create a private val "result receiver" as a field of the activity:
 *
 * private val resultReceiver = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
 *         if (it.resultCode == RESULT_OK) {
 *             recreate()
 *         }
 *     }
 *
 * When launching the new activity, do it like this:
 * resultReceiver.launch(new Intent(context, YourActivity::class.java))
 *
 * - NEW ACTIVITY (this one in this case)
 * When creating the method that returns control to the previous activity (i.e., the one
 * that calls finish()), an empty Intent is created just to transmit data.
 *
 * if (wereFavoritesChanged) {
 *    val data = Intent()
 *    setResult(RESULT_OK, data)
 * }
 * finish()
 *
 * This will trigger a recomposition in the FORMER ACTIVITY.
 * */
class MatchDetailsActivity : ComponentActivity() {
    private val appDatabase = inject<AppDatabase>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val themeState by themeViewModel.state.collectAsStateWithLifecycle()
            val matchDetailsViewModel = koinViewModel<MatchDetailsViewModel>()
            var wereFavoritesChangedFlag by remember { mutableStateOf(false) }
            fetchMatchData(matchDetailsViewModel)
            MatchDetailsScreen(
                state = themeState,
                gameImage = null, //TODO: add game image
                backFunction = {
                    if (wereFavoritesChangedFlag) {
                        /* Informs the caller activity that it needs to recreate */
                        val data = Intent()
                        setResult(RESULT_OK, data)
                    }
                    finish()
                },
                vm = matchDetailsViewModel,
                addMatchToFavorites = this::addMatchToFavorites,
                removeMatchFromFavorites = this::removeMatchFromFavorites,
                setFlag = { wereFavoritesChangedFlag = true }
            )
        }
    }

    private fun fetchMatchData(matchDetailsViewModel: MatchDetailsViewModel) {
        lifecycleScope.launch(Dispatchers.IO) {
            matchDetailsViewModel.selectedMatchId.collect { matchID ->
                if (matchID != null) {
                    val playedMatch = appDatabase.value.matchDao().getMatchFromID(matchID)
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
                    val isDraw = appDatabase.value.matchDao().isDraw(matchID)
                    matchDetailsViewModel.changeTeamDataPackets(teamDataPackets)
                    matchDetailsViewModel.changeTeamUIs(teamDataPackets.map { it.teamUI })
                    matchDetailsViewModel.changePlayedGame(playedGame)
                    matchDetailsViewModel.changeTeams(teams)
                    matchDetailsViewModel.changeTeamsInMatch(teamsInTm)
                    matchDetailsViewModel.changeIsDraw(isDraw)
                    matchDetailsViewModel.changeMatch(playedMatch)
                }
            }
        }
    }

    private fun addMatchToFavorites(matchTmID: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                appDatabase.value.matchDao().setMatchFavorites(matchTmID)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun removeMatchFromFavorites(matchTmID: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                appDatabase.value.matchDao().removeMatchFavorites(matchTmID)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}