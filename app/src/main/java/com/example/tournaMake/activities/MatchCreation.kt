package com.example.tournaMake.activities

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.tournaMake.activities.navgraph.NavigationRoute
import com.example.tournaMake.data.models.MatchCreationViewModel
import com.example.tournaMake.data.models.MatchViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.GuestParticipant
import com.example.tournaMake.sampledata.MainParticipant
import com.example.tournaMake.sampledata.MatchTM
import com.example.tournaMake.sampledata.Team
import com.example.tournaMake.sampledata.TeamInTm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID

fun fetchDataForMatchCreation(vm: MatchCreationViewModel, owner: LifecycleOwner) {
    val appDatabase = inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch (Dispatchers.IO) {
        try {
            val mainProfiles = appDatabase.value.mainProfileDao().getAll()
            vm.changeMainProfiles(mainProfiles)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
fun createMatch(
    gameId: String,
    matchCreationViewModel: MatchCreationViewModel,
    vmScreen: MatchViewModel,
    owner: LifecycleOwner,
    navController: NavController
){
    /**
     * - Create a new team entry.
     * - Create a unique match.
     * - Create a TEAM_IN_TM entity for each team variable.
     * */
    val appDatabase = inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch (Dispatchers.IO) {
        val matchUUID = UUID.randomUUID().toString()
        appDatabase.value.matchDao().insertAll(MatchTM(
            matchTmID = matchUUID,
            favorites = 0,
            date = System.currentTimeMillis(),
            duration = 0,
            isOver = 0,
            gameID = gameId,
            tournamentID = null,
            indexInTournamentTree = null,
        ))
        matchCreationViewModel.teamsSet.value?.forEach {
            val teamUUID = UUID.randomUUID().toString()
            appDatabase.value.teamDao().insert(Team(teamUUID, it.getTeamName()))
            appDatabase.value.teamInTmDao().insert(TeamInTm(teamUUID, matchUUID, 0, 0))
            appDatabase.value.mainParticipantsDao()
                .insertAll(it.getMainProfiles()
                    .map { mainProfile -> MainParticipant(email = mainProfile.email, teamID = teamUUID) }
                )
            appDatabase.value.guestParticipantsDao()
                .insertAll(it.getGuestProfiles()
                    .map { guestProfile -> GuestParticipant(username = guestProfile.username, teamID = teamUUID) }
                )
        }
        // Setting the new match id in the repository
        vmScreen.changeRepository(matchUUID)
        // Navigation to MatchScreen
        withContext(Dispatchers.Main) {
            navController.navigate(route = NavigationRoute.MatchScreen.route)
        }
    }
}