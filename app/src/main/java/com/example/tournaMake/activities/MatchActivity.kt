package com.example.tournaMake.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.MatchScreenViewModel
import com.example.tournaMake.data.models.TeamDataPacket
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.ui.screens.match.MatchScreen
import com.example.tournaMake.ui.screens.match.TeamUIImpl
import com.example.tournaMake.ui.screens.match.testTeam1
import com.example.tournaMake.ui.screens.match.testTeam2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

class MatchActivity : ComponentActivity() {
    private val appDatabase = inject<AppDatabase>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val state = themeViewModel.state.collectAsStateWithLifecycle()
            val matchScreenViewModel = koinViewModel<MatchScreenViewModel>()
            fetchMatchData(matchScreenViewModel)
            MatchScreen(
                state = state.value,
                gameImage = null,
                teamsSet = setOf(testTeam1, testTeam2),
                vm = matchScreenViewModel,
                addMatchToFavorites = this::addMatchToFavorites,
                removeMatchFromFavorites = this::removeMatchToFavorites,
                backFunction = this::goBack
            )
        }
    }
    private fun fetchMatchData(matchScreenViewModel: MatchScreenViewModel) {
        lifecycleScope.launch (Dispatchers.IO) {
            val matchID = matchScreenViewModel.selectedMatchId.value
            if (matchID != null) {

                val playedMatch = appDatabase.value.matchDao().getMatchFromID(matchID)

                Log.d("AIUTO", matchID.toString())
                val playedGame = appDatabase.value.gameDao().getGameFromID(playedMatch.gameID)
                val teamsInTm = appDatabase.value.teamInTmDao().getTeamsInTmFromMatch(playedMatch.matchTmID)
                val teams = appDatabase.value.teamDao().getAll()
                    .filter { team -> teamsInTm
                        .map { teamInTm -> teamInTm.teamID }.contains(team.teamID)
                    }
                val teamDataPackets = teams.map { team ->
                    val mainParticipants = appDatabase.value.mainParticipantsDao().getAllMainParticipantsFromTeam(team.teamID)
                    val guestParticipants = appDatabase.value.guestParticipantsDao().getAllGuestParticipantsFromTeam(team.teamID)
                    val mainProfiles = mainParticipants.map { mainParticipant -> appDatabase.value.mainProfileDao().getProfileByEmail(mainParticipant.email) }
                    val guestProfiles = guestParticipants.map { guestParticipant -> appDatabase.value.guestProfileDao().getFromUsername(guestParticipant.username) }
                    val teamInTM = teamsInTm.first { teamInTm -> teamInTm.teamID == team.teamID }
                    val teamScore = teamInTM.score
                    return@map TeamDataPacket(
                        teamUI = TeamUIImpl(mainProfiles.toSet(), guestProfiles.toSet(), team.name),
                        teamScore = teamScore,
                        teamID = team.teamID,
                        isWinner = teamInTM.isWinner == 1
                    )
                }
                matchScreenViewModel.changeTeamDataPackets(teamDataPackets)
                matchScreenViewModel.changeTeamUIs(teamDataPackets.map { it.teamUI })
                matchScreenViewModel.changePlayedGame(playedGame)
                matchScreenViewModel.changeTeams(teams)
                matchScreenViewModel.changeTeamsInMatch(teamsInTm)
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
    private fun removeMatchToFavorites(matchTmID: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                appDatabase.value.matchDao().removeMatchFavorites(matchTmID)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun goBack() {
        val intent = Intent(this, MatchListActivity::class.java)
        startActivity(intent)
    }
}