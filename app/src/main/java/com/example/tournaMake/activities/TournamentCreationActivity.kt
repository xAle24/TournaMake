package com.example.tournaMake.activities

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.tournaMake.activities.navgraph.NavigationRoute
import com.example.tournaMake.data.models.MatchCreationViewModel
import com.example.tournaMake.data.models.TournamentCreationViewModel
import com.example.tournaMake.data.models.TournamentIDViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.Game
import com.example.tournaMake.sampledata.GuestParticipant
import com.example.tournaMake.sampledata.GuestProfile
import com.example.tournaMake.sampledata.MainParticipant
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.sampledata.MatchTM
import com.example.tournaMake.sampledata.Team
import com.example.tournaMake.sampledata.TeamInTm
import com.example.tournaMake.sampledata.Tournament
import com.example.tournaMake.sampledata.TournamentType
import com.example.tournaMake.ui.screens.match.TeamUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.java.KoinJavaComponent
import java.util.UUID

fun fetchData(vm: MatchCreationViewModel, owner: LifecycleOwner) {
    val db = KoinJavaComponent.inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch (Dispatchers.IO) {
        try {
            val games = db.value.gameDao().getAll()
            val mainProfiles = db.value.mainProfileDao().getAll()
            val guestProfiles = db.value.guestProfileDao().getAll()
            vm.changeGamesList(games ?: emptyList())
            vm.changeMainProfiles(mainProfiles ?: emptyList())
            vm.changeGuestProfiles(guestProfiles ?: emptyList())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
fun fetchAndUpdateGamesList(tournamentCreationViewModel: TournamentCreationViewModel, owner: LifecycleOwner) {
    var gamesList: List<Game>
    val db = KoinJavaComponent.inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            gamesList = db.value.gameDao().getAll()
            tournamentCreationViewModel.changeGameList(gamesList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
fun fetchAndUpdateTournamentTypeList(tournamentCreationViewModel: TournamentCreationViewModel, owner: LifecycleOwner) {
    var typeList: List<TournamentType>
    val db = KoinJavaComponent.inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            typeList = db.value.tournamentTypeDao().getAll()
            tournamentCreationViewModel.changeTournamentTypeList(typeList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
fun fetchAndUpdateGuestProfileList(tournamentCreationViewModel: TournamentCreationViewModel, owner: LifecycleOwner) {
    var guestProfileList: List<GuestProfile>
    val db = KoinJavaComponent.inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            guestProfileList = db.value.guestProfileDao().getAll()
            tournamentCreationViewModel.changeGuestProfileList(guestProfileList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
fun fetchAndUpdateMainProfileList(tournamentCreationViewModel: TournamentCreationViewModel, owner: LifecycleOwner) {
    var mainProfileList: List<MainProfile>
    val db = KoinJavaComponent.inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            mainProfileList = db.value.mainProfileDao().getAll()
            tournamentCreationViewModel.changeMainProfileList(mainProfileList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
fun navigateToTournament(
    teamsSet: Set<TeamUI>,
    selectedGame: Game?,
    selectedTournamentType: TournamentType?,
    selectedTournamentName: String,
    owner: LifecycleOwner,
    navController: NavController
) {
    //TODO salvare in db tutto 1 creare il torneo
    val db = KoinJavaComponent.inject<AppDatabase>(AppDatabase::class.java)
    val tournamentIDViewModel = KoinJavaComponent.inject<TournamentIDViewModel>(TournamentIDViewModel::class.java)
    val tournamentID = UUID.randomUUID().toString()
    if (selectedTournamentType != null && selectedTournamentName != "") {
        val tournament = Tournament(
            tournamentID = tournamentID,
            name = selectedTournamentName,
            favorites = 0,
            scheduledDate = 0,
            isOver = 0,
            tournamentTypeID = selectedTournamentType.tournamentTypeID
        )
        owner.lifecycleScope.launch(Dispatchers.IO) {
            try { //insert tournament in database
                db.value.tournamentDao().insertAll(tournament)
                // Saving to shared preferences
                tournamentIDViewModel.saveTournamentIDInPreferences(tournamentID)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    if (selectedGame != null) {
        val shuffledList = teamsSet.shuffled()
        val matches = generateSequence(0) { it + 2 }
            .take(shuffledList.size / 2)
            .map { index -> Pair(shuffledList[index], shuffledList[index + 1]) }
            .toList()
        owner.lifecycleScope.launch(Dispatchers.IO) {
            matches.forEach { match ->
                val firstTeamID = UUID.randomUUID().toString()
                val secondTeamID = UUID.randomUUID().toString()
                val teams: List<Team> = listOf(
                    Team(
                        teamID = firstTeamID,
                        name = match.first.getTeamName(),
                        /*isWinner = 'F',
                        score = 0*/
                    ),
                    Team(
                        teamID = secondTeamID,
                        name = match.second.getTeamName(),
                        /*isWinner = 'F',
                        score = 0*/
                    )
                )
                db.value.teamDao().insertAll(teams)
                val matchID = UUID.randomUUID().toString()
                val matchCurr = MatchTM(
                    matchTmID = matchID,
                    date = 0,
                    duration = 0,
                    favorites = 0,
                    gameID = selectedGame.gameID,
                    isOver = 0,
                    tournamentID = tournamentID
                )
                db.value.matchDao().insertAll(matchCurr)
                teams.forEach { team ->
                    val teamTm = TeamInTm(teamID = team.teamID, matchTmID = matchID, isWinner = 0, score = 0)
                    db.value.teamInTmDao().insert(teamTm)
                }
                match.first.getGuestProfiles().forEach { profile ->
                    val guestProfile = GuestParticipant(
                        username = profile.username,
                        teamID = firstTeamID
                    )
                    db.value.guestParticipantsDao().insertAll(guestProfile)
                }
                match.first.getMainProfiles().forEach {profile ->
                    val mainProfile = MainParticipant(
                        email = profile.email,
                        teamID = firstTeamID
                    )
                    db.value.mainParticipantsDao().insertAll(mainProfile)
                }
                match.second.getMainProfiles().forEach {profile ->
                    val mainProfile = MainParticipant(
                        email = profile.username,
                        teamID = secondTeamID
                    )
                    db.value.mainParticipantsDao().insertAll(mainProfile)
                }
                match.second.getGuestProfiles().forEach {profile ->
                    val guestProfile = GuestParticipant(
                        username = profile.username,
                        teamID = secondTeamID
                    )
                    db.value.guestParticipantsDao().insertAll(guestProfile)
                }
            }
        }
    }
    navController.navigate(NavigationRoute.TournamentScreen.route)
}