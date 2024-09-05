package com.example.tournaMake.ui.screens.tournament

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.tournaMake.activities.MatchAsCompetingTeams
import com.example.tournaMake.activities.TournamentManagerUpdateRequest
import com.example.tournaMake.activities.createBracket
import com.example.tournaMake.activities.fetchStuffForTournament
import com.example.tournaMake.activities.getMatchesNamesAsCompetingTeams
import com.example.tournaMake.activities.navgraph.NavigationRoute
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.data.models.TournamentDataViewModel
import com.example.tournaMake.data.models.TournamentIDViewModel
import com.example.tournaMake.mylibrary.ui.SingleEliminationBracket
import com.example.tournaMake.sampledata.TournamentMatchData
import com.example.tournaMake.tournamentmanager.TournamentManager
import com.example.tournaMake.tournamentmanager.TournamentManagerV2
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TournamentScreen(
    navController: NavController,
    owner: LifecycleOwner,
) {
    val refresh = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<Boolean>("refresh")
        ?.observeAsState()
    val refreshString = navController.currentBackStackEntry?.arguments?.getString("refresh")
    var refresh2 = refreshString?.toBoolean() ?: true

    // Initial variables
    var tournamentManager: TournamentManagerV2? = null

    // Former activity code
    val themeViewModel = koinViewModel<ThemeViewModel>()
    val state by themeViewModel.state.collectAsStateWithLifecycle()
    val tournamentIDViewModel = koinViewModel<TournamentIDViewModel>()
    val tournamentID = tournamentIDViewModel.tournamentID.collectAsStateWithLifecycle()
    val tournamentDataViewModel = koinViewModel<TournamentDataViewModel>()

    if (refresh?.value == true || refresh2) {
        // Trigger the refresh logic here, for example, by reloading data
        fetchStuffForTournament(tournamentID.value, tournamentDataViewModel, owner)

        // Reset the refresh flag after the UI is refreshed
        navController.currentBackStackEntry?.savedStateHandle?.set("refresh", false)
        refresh2 = false
    }

    // Observing live data
    val tournamentMatchLiveData by tournamentDataViewModel.tournamentMatchesAndTeamsLiveData.observeAsState()
    val dbMatches by tournamentDataViewModel.dbMatchesInTournament.observeAsState()
    val tournamentName by tournamentDataViewModel.tournamentName.observeAsState()

    if (tournamentMatchLiveData != null && dbMatches != null && tournamentName != null) {
        tournamentManager =
            TournamentManagerV2(tournamentMatchLiveData!!, dbMatches!!, tournamentName!!)
    }

    if (tournamentManager != null) {
        val privateBracket = tournamentManager.produceBracket()
        val bracket by remember { mutableStateOf(privateBracket) }

        // UI Code
        key(bracket) {
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
                    SingleEliminationBracket(bracket = bracket, navController = navController)

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
                /*ModifyMatchesAlert(
                    openDialog = isAlertVisible,
                    onDismiss = { isAlertVisible = false },
                    matchesAndTeams = data.value,
                    onConfirmCallback = {
                        tournamentManager.updateMatch(it)
                        bracket = tournamentManager.refreshBracket()
                        data.value = getMatchesNamesAsCompetingTeams(tournamentManager.refreshTournamentDataList())
                        Log.d("DEV", "Trying to refresh...")
                    },
                )*/
            }
        }
    }
}

enum class RadioButtonStates { None, First, Second }

@Composable
fun ModifyMatchesAlert(
    openDialog: Boolean,
    onDismiss: () -> Unit,
    matchesAndTeams: List<MatchAsCompetingTeams>,
    onConfirmCallback: (TournamentManagerUpdateRequest) -> Unit,
) {
    val matchID = remember { mutableStateOf("") }
    val firstTeamID = remember {
        mutableStateOf("")
    }
    val secondTeamID = remember {
        mutableStateOf("")
    }
    val firstTeamName = remember { mutableStateOf("") }
    val secondTeamName = remember { mutableStateOf("") }
    val firstTeamScore = remember { mutableIntStateOf(0) }
    val secondTeamScore = remember { mutableIntStateOf(0) }
    val winner = remember { mutableStateOf(RadioButtonStates.None) }
    if (openDialog) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            title = {
                Text(text = "Change Match data:")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    MatchSelectionMenu(
                        list = matchesAndTeams,
                        callback = {
                            matchID.value = it.matchID
                            firstTeamID.value = it.firstTeamID
                            secondTeamID.value = it.secondTeamID
                            firstTeamName.value = it.firstTeamName
                            secondTeamName.value = it.secondTeamName
                        }
                    )
                    // A row for each team name
                    // First team row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text("Winner?", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = firstTeamName.value,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth(0.8f),
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                        RadioButton(
                            selected = winner.value == RadioButtonStates.First,
                            onClick = {
                                if (winner.value == RadioButtonStates.First) {
                                    winner.value = RadioButtonStates.None
                                } else {
                                    winner.value = RadioButtonStates.First
                                }
                            }
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedTextField(
                            value = firstTeamScore.intValue.toString(),
                            onValueChange = {
                                if (it.isNotEmpty() && it.length < 9)
                                    firstTeamScore.intValue = it.toInt()
                                else if (it.isEmpty())
                                    secondTeamScore.intValue = 0
                            },
                            label = {
                                Text("First team score")
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    // Row for second team element
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text("Winner?", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = secondTeamName.value,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth(0.8f),
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                        RadioButton(
                            selected = winner.value == RadioButtonStates.Second,
                            onClick = {
                                if (winner.value == RadioButtonStates.Second) {
                                    winner.value = RadioButtonStates.None
                                } else {
                                    winner.value = RadioButtonStates.Second
                                }
                            },
                        )

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedTextField(
                            value = secondTeamScore.intValue.toString(),
                            onValueChange = {
                                if (it.isNotEmpty() && it.length < 9)
                                    secondTeamScore.intValue = it.toInt()
                                else if (it.isEmpty())
                                    secondTeamScore.intValue = 0
                            },
                            label = {
                                Text("Second team score")
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    onConfirmCallback(
                        TournamentManagerUpdateRequest(
                            firstTeamName = firstTeamName.value,
                            secondTeamName = secondTeamName.value,
                            isFirstTeamWinner = winner.value == RadioButtonStates.First,
                            isSecondTeamWinner = winner.value == RadioButtonStates.Second,
                            firstTeamScore = firstTeamScore.intValue,
                            secondTeamScore = secondTeamScore.intValue
                        )
                    )
                    onDismiss()
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { onDismiss() }) {
                    Text("Cancel")
                }
            }
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MatchSelectionMenu(
    list: List<MatchAsCompetingTeams>,
    callback: (MatchAsCompetingTeams) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    // Assuming that the list will always contain at least one value
    var selectedMatch by remember { mutableStateOf(list[0]) }
    Log.d("DEV", "List of competing teams in Alert Selector = $list")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            },
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
        ) {
            TextField(
                value = "${selectedMatch.firstTeamName} vs ${selectedMatch.secondTeamName}", // TODO: check here
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.onSecondary,
                    unfocusedContainerColor = Color.White.copy(alpha = 0.7f)
                ),
                label = {
                    Text(
                        text = "Select Match to update",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
            ) {
                list.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = "${item.firstTeamName} vs ${item.secondTeamName}") },
                        onClick = {
                            selectedMatch = item
                            expanded = false
                            callback(item)
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun TournamentPreview() {
    TournamentScreen(
        navController = NavController(LocalContext.current),
        owner = ComponentActivity()
    )
}
