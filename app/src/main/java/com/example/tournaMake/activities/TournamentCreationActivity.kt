package com.example.tournaMake.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.MatchCreationViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.data.models.TournamentCreationViewModel
import com.example.tournaMake.data.models.TournamentIDViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.Game
import com.example.tournaMake.sampledata.GuestParticipantScore
import com.example.tournaMake.sampledata.GuestProfile
import com.example.tournaMake.sampledata.MainParticipantScore
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.sampledata.MatchTM
import com.example.tournaMake.sampledata.Team
import com.example.tournaMake.sampledata.TeamInTm
import com.example.tournaMake.sampledata.Tournament
import com.example.tournaMake.sampledata.TournamentType
import com.example.tournaMake.ui.screens.match.TeamUI
import com.example.tournaMake.ui.screens.tournament.TournamentCreationScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

class TournamentCreationActivity : ComponentActivity() {
    private var appDatabase: AppDatabase? = get<AppDatabase>()
    // TODO: Check if it works
    private val tournamentIDViewModel = get<TournamentIDViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val state = themeViewModel.state.collectAsStateWithLifecycle()
            val tournamentCreationViewModel = koinViewModel<TournamentCreationViewModel>()
            val matchCreationViewModel = koinViewModel<MatchCreationViewModel>()
            fetchAndUpdateGamesList(tournamentCreationViewModel)
            fetchAndUpdateTournamentTypeList(tournamentCreationViewModel)
            fetchAndUpdateGuestProfileList(tournamentCreationViewModel)
            fetchAndUpdateMainProfileList(tournamentCreationViewModel)
            fetchData(matchCreationViewModel)
            TournamentCreationScreen(
                state = state.value,
                teamsStateFlow = matchCreationViewModel.teamsSet,
                addTeam = matchCreationViewModel::addTeam,
                removeTeam = matchCreationViewModel::removeTeam,
                tournamentCreationViewModel.gamesListLiveData,
                tournamentCreationViewModel.tournamentListLiveData,
                tournamentCreationViewModel.mainProfileListLiveData,
                tournamentCreationViewModel.guestProfileListLiveData,
                navigateToTournament = this::navigateToTournament,
                backFunction = this::finish
            )
        }
    }

    private fun fetchData(vm: MatchCreationViewModel) {
        lifecycleScope.launch (Dispatchers.IO) {
            try {
                val games = appDatabase?.gameDao()?.getAll()
                val mainProfiles = appDatabase?.mainProfileDao()?.getAll()
                val guestProfiles = appDatabase?.guestProfileDao()?.getAll()
                vm.changeGamesList(games ?: emptyList())
                vm.changeMainProfiles(mainProfiles ?: emptyList())
                vm.changeGuestProfiles(guestProfiles ?: emptyList())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun navigateToTournament(
        teamsSet: Set<TeamUI>,
        selectedGame: Game?,
        selectedTournamentType: TournamentType?,
        selectedTournamentName: String
    ) {
        //TODO salvare in db tutto 1 creare il torneo
        val tournamentID = UUID.randomUUID().toString()
        if (selectedTournamentType != null && selectedTournamentName != "") {
            val tournament = Tournament(
                tournamentID = tournamentID,
                name = selectedTournamentName,
                favorites = 'F',
                locationLatitude = 0.0f,
                locationLongitude = 0.0f,
                scheduledDate = 0,
                status = 0,
                tournamentTypeID = selectedTournamentType.tournamentTypeID
            )
            lifecycleScope.launch(Dispatchers.IO) {
                try { //insert tournament in database
                    appDatabase?.tournamentDao()?.insertAll(tournament)
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
            lifecycleScope.launch(Dispatchers.IO) {
                matches.forEach { match ->
                    val firstTeamID = UUID.randomUUID().toString()
                    val secondTeamID = UUID.randomUUID().toString()
                    val teams: List<Team> = listOf(
                        Team(
                            teamID = firstTeamID,
                            name = match.first.getTeamName(),
                            isWinner = 'F',
                            score = 0
                        ),
                        Team(
                            teamID = secondTeamID,
                            name = match.second.getTeamName(),
                            isWinner = 'F',
                            score = 0
                        )
                    )
                    appDatabase?.teamDao()?.insertAll(teams)
                    val matchID = UUID.randomUUID().toString()
                    val matchCurr = MatchTM(
                        matchTmID = matchID,
                        date = 0,
                        duration = 0,
                        favorites = "F",
                        gameID = selectedGame.gameID,
                        status = 0,
                        tournamentID = tournamentID
                    )
                    appDatabase?.matchDao()?.insertAll(matchCurr)
                    teams.forEach { team ->
                        val teamTm = TeamInTm(teamID = team.teamID, matchTmID = matchID)
                        appDatabase?.teamInTmDao()?.insert(teamTm)
                    }
                    match.first.getGuestProfiles().forEach { profile ->
                        val guestProfile = GuestParticipantScore(
                            username = profile.username,
                            teamID = firstTeamID,
                            score = 0
                        )
                        appDatabase?.matchScoreGuestDao()?.insertAll(guestProfile)
                    }
                    match.first.getMainProfiles().forEach {profile ->
                        val mainProfile = MainParticipantScore(
                            email = profile.email,
                            teamID = firstTeamID,
                            score = 0
                        )
                        appDatabase?.matchScoreMainDao()?.insertAll(mainProfile)
                    }
                    match.second.getMainProfiles().forEach {profile ->
                        val mainProfile = MainParticipantScore(
                            email = profile.username,
                            teamID = secondTeamID,
                            score = 0
                        )
                        appDatabase?.matchScoreMainDao()?.insertAll(mainProfile)
                    }
                    match.second.getGuestProfiles().forEach {profile ->
                        val guestProfile = GuestParticipantScore(
                            username = profile.username,
                            teamID = secondTeamID,
                            score = 0
                        )
                        appDatabase?.matchScoreGuestDao()?.insertAll(guestProfile)
                    }
                }
            }
        }
        val intent = Intent(this, TournamentActivity::class.java)
        startActivity(intent)
    }

    private fun fetchAndUpdateGamesList(tournamentCreationViewModel: TournamentCreationViewModel) {
        var gamesList: List<Game>
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                gamesList = appDatabase?.gameDao()?.getAll() ?: emptyList()
                tournamentCreationViewModel.changeGameList(gamesList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchAndUpdateTournamentTypeList(tournamentCreationViewModel: TournamentCreationViewModel) {
        var typeList: List<TournamentType>
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                typeList = appDatabase?.tournamentTypeDao()?.getAll() ?: emptyList()
                tournamentCreationViewModel.changeTournamentTypeList(typeList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchAndUpdateGuestProfileList(tournamentCreationViewModel: TournamentCreationViewModel) {
        var guestProfileList: List<GuestProfile>
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                guestProfileList = appDatabase?.guestProfileDao()?.getAll() ?: emptyList()
                tournamentCreationViewModel.changeGuestProfileList(guestProfileList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchAndUpdateMainProfileList(tournamentCreationViewModel: TournamentCreationViewModel) {
        var mainProfileList: List<MainProfile>
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                mainProfileList = appDatabase?.mainProfileDao()?.getAll() ?: emptyList()
                tournamentCreationViewModel.changeMainProfileList(mainProfileList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
