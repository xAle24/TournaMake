package com.example.tournaMake.activities

import android.view.Window
import androidx.core.view.WindowCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.TournamentDataViewModel
import com.example.tournaMake.mylibrary.displaymodels.BracketDisplayModel
import com.example.tournaMake.mylibrary.displaymodels.BracketMatchDisplayModel
import com.example.tournaMake.mylibrary.displaymodels.BracketRoundDisplayModel
import com.example.tournaMake.mylibrary.displaymodels.BracketTeamDisplayModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.MatchTM
import com.example.tournaMake.sampledata.TeamInTm
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
            //tournamentDataViewModel.changeMatchesList(tournamentMatchesAndTeamsData)
            tournamentDataViewModel.refresh(tournament.name, tournamentID)
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
            score = it.key.score,
            isWinner = it.key.isWinner
        )
    }
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            appDatabase.teamInTmDao().insertAll(*teamInTms.toTypedArray())
        } catch (e: Exception) {
            e.printStackTrace()
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