package com.example.tournaMake.ui.screens.tournament

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.tournaMake.R
import com.example.tournaMake.activities.fetchAndUpdateMainProfileList
import com.example.tournaMake.activities.fetchAndUpdateTournamentTypeList
import com.example.tournaMake.activities.fetchData
import com.example.tournaMake.activities.navigateToTournament
import com.example.tournaMake.data.models.MatchCreationViewModel
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.data.models.TournamentCreationViewModel
import com.example.tournaMake.sampledata.Game
import com.example.tournaMake.sampledata.TournamentType
import com.example.tournaMake.ui.screens.common.BasicScreenWithAppBars
import com.example.tournaMake.ui.screens.match.TeamContainer
import com.example.tournaMake.ui.screens.match.TeamUIImpl
import com.example.tournaMake.ui.theme.getThemeColors
import org.koin.androidx.compose.koinViewModel

@Composable
fun TournamentCreationScreen(
    navController: NavController,
    owner: LifecycleOwner,
) {
    val themeViewModel = koinViewModel<ThemeViewModel>()
    val state by themeViewModel.state.collectAsStateWithLifecycle()
    val tournamentCreationViewModel = koinViewModel<TournamentCreationViewModel>()
    val matchCreationViewModel = koinViewModel<MatchCreationViewModel>()
    val context = LocalContext.current
    fetchAndUpdateTournamentTypeList(tournamentCreationViewModel, owner)
    fetchAndUpdateMainProfileList(tournamentCreationViewModel, owner)
    fetchData(matchCreationViewModel, owner)
    BasicScreenWithAppBars(
        state = state,
        backFunction = { navController.navigateUp() },
        showTopBar = true,
        showBottomBar = false
    ) {
        val colorConstants = getThemeColors(themeState = state)
        val imageLogoId =
            if (state.theme == ThemeEnum.Dark) R.drawable.light_writings else R.drawable.dark_writings

        /* Variable containing all the created teams */
        val teamsSet by matchCreationViewModel.teamsSet.observeAsState()
        var selectedGame by remember { mutableStateOf<Game?>(null) }
        var selectedTournamentType by remember { mutableStateOf<TournamentType?>(null) }
        var selectedTournamentName by remember { mutableStateOf("") }

        val gamesList = tournamentCreationViewModel.gamesListLiveData.observeAsState()
        val tournamentTypeList = tournamentCreationViewModel.tournamentListLiveData.observeAsState()
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = imageLogoId),
                contentDescription = "top app bar background",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.15f)
            )
            Spacer(modifier = Modifier.height(30.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize(0.9f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(color = MaterialTheme.colorScheme.secondary.copy(0.8f))
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                TextField(
                    value = selectedTournamentName,
                    onValueChange = { selectedTournamentName = it },
                    label = { Text("Tournament Name", fontSize = 20.sp) },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                            alpha = 0.8f
                        ),
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                        cursorColor = MaterialTheme.colorScheme.onPrimary,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    textStyle = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
                Spacer(modifier = Modifier.height(40.dp))

                SelectionMenuGame(gamesList) { selectedGame = it }

                Spacer(modifier = Modifier.height(40.dp))

                SelectionMenuTournamentType(tournamentTypeList) { selectedTournamentType = it }

                Spacer(modifier = Modifier.height(40.dp))

                /*
                * Here begins the huge part of the team container
                * */
                TeamContainer(
                    removeTeam = matchCreationViewModel::removeTeam
                )
                Button(
                    onClick = {
                        matchCreationViewModel.addTeam(TeamUIImpl(emptySet(), emptySet(), ""))
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .height(60.dp)
                        .background(colorConstants.getButtonBackground())
                        .fillMaxWidth(0.9f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(3.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text("Add team")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (selectedGame == null) {
                            Toast.makeText(context, "Select a game before continuing", Toast.LENGTH_SHORT).show()
                        } else if (selectedTournamentType == null) {
                            Toast.makeText(context, "Select a tournament type before continuing", Toast.LENGTH_SHORT).show()
                        } else if (selectedTournamentName == "") {
                            Toast.makeText(context, "Select a name before continuing", Toast.LENGTH_SHORT).show()
                        } else if (selectedTournamentType!!.name == "Double Bracket") {
                            Toast.makeText(context, "This mode will be added in the next update", Toast.LENGTH_SHORT).show()
                        } else if (teamsSet == null || teamsSet!!.size < 2) {
                            Toast.makeText(context, "At least 2 teams must be present", Toast.LENGTH_SHORT).show()
                        } else {
                            navigateToTournament(
                                teamsSet!!,
                                selectedGame,
                                selectedTournamentType,
                                selectedTournamentName,
                                navController = navController,
                                owner = owner
                            )
                        }
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .height(60.dp)
                        .fillMaxWidth(0.9f)
                        .background(colorConstants.getButtonBackground()),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(3.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text("Start tournament")
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionMenuGame(list: State<List<Game>?>, gameCallback: (Game) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(list.value?.get(0)) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            },
            modifier = Modifier
                .align(Alignment.Center)
                .background(MaterialTheme.colorScheme.onSecondary)
        ) {
            TextField(
                value = selectedText?.name ?: "",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(0.9f),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                ),
                label = {
                    Text(
                        text = "Select Game",
                        fontSize = 20.sp
                    )
                },
                textStyle = MaterialTheme.typography.headlineMedium
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
            ) {
                list.value?.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item.name) },
                        onClick = {
                            selectedText = item
                            expanded = false
                            gameCallback(item)
                            //Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                            // TODO: add update for the form to complete
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectionMenuTournamentType(
    list: State<List<TournamentType>?>,
    typeCallback: (TournamentType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(list.value?.get(0)) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            },
            modifier = Modifier.align(Alignment.Center)
        ) {
            TextField(
                value = selectedText?.name ?: "",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(0.9f),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                ),
                label = {
                    Text(
                        text = "Select Type",
                        fontSize = 20.sp
                    )
                },
                textStyle = MaterialTheme.typography.headlineMedium
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
            ) {
                list.value?.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item.name) },
                        onClick = {
                            selectedText = item
                            expanded = false
                            typeCallback(item)
                            //Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                            // TODO: add update for the form to complete
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
