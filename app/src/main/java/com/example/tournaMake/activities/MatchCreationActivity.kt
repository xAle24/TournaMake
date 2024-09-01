package com.example.tournaMake.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.MatchCreationViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.GuestParticipant
import com.example.tournaMake.sampledata.MainParticipant
import com.example.tournaMake.sampledata.MatchTM
import com.example.tournaMake.sampledata.Team
import com.example.tournaMake.sampledata.TeamInTm
import com.example.tournaMake.ui.screens.match.MatchCreationScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

class MatchCreationActivity : ComponentActivity() {
    private val appDatabase = inject<AppDatabase>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val state = themeViewModel.state.collectAsStateWithLifecycle()
            val matchCreationViewModel = koinViewModel<MatchCreationViewModel>()
            fetchData(matchCreationViewModel)
            MatchCreationScreen(
                state = state.value,
                backFunction = this::goBack,
                gamesListLiveData = matchCreationViewModel.games,
                teamsSetStateFlow = matchCreationViewModel.teamsSet,
                mainProfilesLiveData = matchCreationViewModel.mainProfiles,
                guestProfilesLiveData = matchCreationViewModel.guestProfiles,
                addTeam = matchCreationViewModel::addTeam,
                removeTeam = matchCreationViewModel::removeTeam,
                createMatchCallback = { gameId ->
                    this.createMatch(gameId, matchCreationViewModel)
                    navigateToMatch()
                }
            )
        }
    }

    private fun fetchData(vm: MatchCreationViewModel) {
        lifecycleScope.launch (Dispatchers.IO) {
            try {
                val games = appDatabase.value.gameDao().getAll()
                val mainProfiles = appDatabase.value.mainProfileDao().getAll()
                val guestProfiles = appDatabase.value.guestProfileDao().getAll()
                vm.changeGamesList(games)
                vm.changeMainProfiles(mainProfiles)
                vm.changeGuestProfiles(guestProfiles)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun createMatch(
        gameId: String,
        matchCreationViewModel: MatchCreationViewModel
    ){
        /**
         * - Create a new team entry.
         * - Create a unique match.
         * - Create a TEAM_IN_TM entity for each team variable.
         * */
        lifecycleScope.launch (Dispatchers.IO) {
            val matchUUID = UUID.randomUUID().toString()
            appDatabase.value.matchDao().insertAll(MatchTM(
                matchTmID = matchUUID,
                favorites = 0,
                date = System.currentTimeMillis(),
                duration = 0,
                status = 0,
                gameID = gameId,
                tournamentID = null
            ))
            matchCreationViewModel.teamsSet.value.forEach {
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
        }
    }
    private fun navigateToMatch() {
        val intent = Intent(this, MatchActivity::class.java)
        startActivity(intent)
    }
    private fun goBack() {
        finish()
    }
}