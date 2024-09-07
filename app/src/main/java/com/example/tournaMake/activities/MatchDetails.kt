package com.example.tournaMake.activities

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.MatchDetailsViewModel
import com.example.tournaMake.data.models.TeamDataPacket
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.ui.screens.match.TeamUIImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

fun fetchMatchData(matchDetailsViewModel: MatchDetailsViewModel, owner: LifecycleOwner) {
    val appDatabase = inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch(Dispatchers.IO) {
        matchDetailsViewModel.selectedMatchId.collect { matchID ->
            if (matchID != null) {
                val playedMatch = appDatabase.value.matchDao().getMatchFromID(matchID)
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
                matchDetailsViewModel.changeRepository(playedMatch.gameID)
                matchDetailsViewModel.changeTeams(teams)
                matchDetailsViewModel.changeTeamsInMatch(teamsInTm)
                matchDetailsViewModel.changeIsDraw(isDraw)
                matchDetailsViewModel.changeMatch(playedMatch)
            }
        }
    }
}