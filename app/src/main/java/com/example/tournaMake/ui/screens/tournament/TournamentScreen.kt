package com.example.tournaMake.ui.screens.tournament

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.tournaMake.activities.endTournament
import com.example.tournaMake.activities.fetchStuffForTournament
import com.example.tournaMake.activities.insertNewMatches
import com.example.tournaMake.activities.insertNewTeamInTms
import com.example.tournaMake.activities.navgraph.NavigationRoute
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.data.models.TournamentDataViewModel
import com.example.tournaMake.data.models.TournamentIDViewModel
import com.example.tournaMake.mylibrary.ui.SingleEliminationBracket
import com.example.tournaMake.tournamentmanager.TournamentManagerV2
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.runBlocking
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TournamentScreen(
    navController: NavController,
    owner: LifecycleOwner,
) {
    // Initial variables
    var tournamentManager: TournamentManagerV2? = null

    // Former activity code
    val themeViewModel = koinViewModel<ThemeViewModel>()
    val state by themeViewModel.state.collectAsStateWithLifecycle()
    val tournamentIDViewModel = koinViewModel<TournamentIDViewModel>()
    val tournamentID = tournamentIDViewModel.tournamentID.collectAsStateWithLifecycle()
    val tournamentDataViewModel = koinViewModel<TournamentDataViewModel>()

    // Trigger the refresh logic here, for example, by reloading data
    fetchStuffForTournament(tournamentID.value, tournamentDataViewModel, owner)

    // Observing live data
    val dbMatches by tournamentDataViewModel.dbMatchesInTournament.observeAsState()
    val tournamentMatchLiveData by tournamentDataViewModel.tournamentMatchesAndTeamsLiveData.observeAsState()
    val tournamentName by tournamentDataViewModel.tournamentName.observeAsState()

    Log.d("DEV-TOURNAMENT-SCREEN", "Tournament name: $tournamentName")

    if (tournamentMatchLiveData != null && dbMatches != null && tournamentName != null) {
        tournamentManager =
            TournamentManagerV2(tournamentMatchLiveData!!, dbMatches!!, tournamentName!!)
        if (tournamentManager.shouldOtherMatchesBeCreated()) {
            val matches = tournamentManager.generateNextRoundMatches()
            runBlocking {
                insertNewMatches(matches, owner) // asynchronous
            }
        } else {
            val danglingTeams = tournamentManager.getDanglingTeams()
            Log.d("DEV-DANGLING", "Dangling teams: $danglingTeams")
            val matchesWithAvailableSlots = tournamentManager.filterMatchesWithAvailableSlots()
            if (matchesWithAvailableSlots.isNotEmpty() && danglingTeams.isNotEmpty()) {
                val mapOfTeamsAndMatches = tournamentManager.associateTeamsWithNextMatches(
                    teams = danglingTeams,
                    newMatches = matchesWithAvailableSlots
                )
                if (mapOfTeamsAndMatches.isNotEmpty()) {
                    runBlocking {
                        insertNewTeamInTms(mapOfTeamsAndMatches, owner) // asynchronous
                    }
                }
            }
        }
        if (tournamentManager.isTournamentOver()) {
            runBlocking {
                endTournament( // asynchronous
                    tournamentID = tournamentID.value,
                    winnerTeamID = tournamentManager.getTournamentWinner().teamID,
                    owner = owner
                )
            }
        }
    }

    if (tournamentManager != null) {
        val privateBracket = tournamentManager.produceBracket()
        var show by remember {
            mutableStateOf(false)
        }

        // Trigger a check (if needed)
        LaunchedEffect(tournamentManager.isTournamentOver()) {
            show = tournamentManager.isTournamentOver()
        }

        if (show) {
            AlertDialog(
                onDismissRequest = { show = false },
                confirmButton = {
                    Button(onClick = { show = false }) {
                        Text("Good stuff!")
                    }
                },
                title = {
                    Text("Tournament is over! ${tournamentManager.getTournamentWinner().name} WIN!")
                }
            )
        }

        // UI Code
        BasicScreenWithTheme(
            state = state
        ) {
            ConfigureTransparentSystemBars()

            Surface(
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f), // the background behind the whole bracket system
                modifier = Modifier
                    .systemBarsPadding(),
            ) {

                /** For now we only handle a single elimination tournament. */
                SingleEliminationBracket(bracket = privateBracket, navController = navController)

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomStart
                ) {
                    FloatingActionButton(
                        onClick = {
                            /** Looks in the back stack for the tournaments list screen */
                            Log.d(
                                "DEV-TOURNAMENT-SCREEN",
                                "Previous stack entry is: ${navController.previousBackStackEntry?.destination?.route}"
                            )
                            if (!navController.popBackStack(
                                    route = NavigationRoute.TournamentsListScreen.route,
                                    inclusive = false, // maybe this will force recomposition?
                                    saveState = false
                                )
                            ) {
                                throw IllegalStateException("Could not pop back to tournament list screen!")
                            }
                        },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Add"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfigureTransparentSystemBars() {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()

    DisposableEffect(systemUiController, useDarkIcons) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons,
        )

        onDispose { }
    }
}
