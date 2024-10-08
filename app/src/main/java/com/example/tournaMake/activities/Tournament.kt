package com.example.tournaMake.activities

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.TournamentDataViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.MatchTM
import com.example.tournaMake.sampledata.TeamInTm
import com.example.tournaMake.sampledata.TournamentMatchData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

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
            val tournament = appDatabase.tournamentDao().getTournamentFromID(tournamentID)
            //tournamentDataViewModel.changeMatchesList(tournamentMatchesAndTeamsData)
            tournamentDataViewModel.refresh(tournament.name)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun insertNewMatches(
    matches: List<MatchTM>,
    owner: LifecycleOwner
) {
    val appDatabase by inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            /**
             * Spread operator * allows passing a list to a function
             * that takes a vararg parameter.
             * See https://www.dhiwise.com/post/how-to-convert-kotlin-list-to-vararg-step-by-step-guide
             * */
            appDatabase.matchDao().insertAll(*matches.toTypedArray())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun insertNewTeamInTms(
    matchesAndTeams: Map<TournamentMatchData, MatchTM>,
    owner: LifecycleOwner
) {
    val appDatabase by inject<AppDatabase>(AppDatabase::class.java)
    val teamInTms = matchesAndTeams.map {
        TeamInTm(
            teamID = it.key.teamID,
            matchTmID = it.value.matchTmID,
            score = 0,
            isWinner = 0
        )
    }
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            appDatabase.teamInTmDao().insertAll(*teamInTms.toTypedArray())
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}

fun endTournament(
    tournamentID: String,
    winnerTeamID: String,
    owner: LifecycleOwner
) {
    /* First check if the tournament is already over */
    val appDatabase by inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            val isTournamentAlreadyOver = appDatabase
                .tournamentDao()
                .getTournamentFromID(tournamentID)
                .isOver == 1
            if (!isTournamentAlreadyOver) {
                appDatabase.tournamentDao().endTournament(tournamentID)
                appDatabase.tournamentDao().incrementWonTournamentsNumberOfMembersInTeam(winnerTeamID)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun addTournamentToFavorites(
    tournamentID: String,
    owner: LifecycleOwner
) {
    val appDatabase by inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch (Dispatchers.IO) {
        try {
            appDatabase.tournamentDao().setTournamentFavorite(tournamentID, 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun removeTournamentFromFavorites(
    tournamentID: String,
    owner: LifecycleOwner
) {
    val appDatabase by inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch (Dispatchers.IO) {
        try {
            appDatabase.tournamentDao().setTournamentFavorite(tournamentID, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}