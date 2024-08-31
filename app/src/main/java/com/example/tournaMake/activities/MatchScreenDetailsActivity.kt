package com.example.tournaMake.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.MatchDetailsViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.ui.screens.match.TeamUIImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

class MatchScreenDetailsActivity: ComponentActivity() {
    private val appDatabase = inject<AppDatabase>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val matchDetailsViewModel = koinViewModel<MatchDetailsViewModel>()
            fetchMatchData(matchDetailsViewModel)
        }
    }

    private fun fetchMatchData(matchDetailsViewModel: MatchDetailsViewModel) {
        lifecycleScope.launch (Dispatchers.IO) {
            val matchID = matchDetailsViewModel.selectedMatchId.value
            if (matchID != null) {
                val playedMatch = appDatabase.value.matchDao().getMatchFromID(matchID)
                val playedGame = appDatabase.value.gameDao().getGameFromID(playedMatch.gameID)
                val teamsInTm = appDatabase.value.teamInTmDao().getTeamsInTmFromMatch(playedMatch.matchTmID)
                val teams = appDatabase.value.teamDao().getAll()
                    .filter { team -> teamsInTm
                        .map { teamInTm -> teamInTm.teamID }.contains(team.teamID)
                    }
                val teamUIs = teams.map { team ->
                    val mainParticipants = appDatabase.value.mainParticipantsDao().getAllMainParticipantsFromTeam(team.teamID)
                    val guestParticipants = appDatabase.value.guestParticipantsDao().getAllGuestParticipantsFromTeam(team.teamID)
                    val mainProfiles = mainParticipants.map { mainParticipant -> appDatabase.value.mainProfileDao().getProfileByEmail(mainParticipant.email) }
                    val guestProfiles = guestParticipants.map { guestParticipant -> appDatabase.value.guestProfileDao().getFromUsername(guestParticipant.username) }
                    return@map TeamUIImpl(mainProfiles.toSet(), guestProfiles.toSet(), team.name)
                }
                matchDetailsViewModel.changeTeamUIs(teamUIs)
                matchDetailsViewModel.changePlayedGame(playedGame)
                matchDetailsViewModel.changeTeams(teams)
                matchDetailsViewModel.changeTeamsInMatch(teamsInTm)
            }
        }
    }
}