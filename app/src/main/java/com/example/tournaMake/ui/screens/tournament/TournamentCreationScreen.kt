package com.example.tournaMake.ui.screens.tournament

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.example.tournaMake.R
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.sampledata.Game
import com.example.tournaMake.sampledata.GuestProfile
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.sampledata.TournamentType
import com.example.tournaMake.ui.screens.common.BasicScreenWithAppBars
import com.example.tournaMake.ui.screens.match.TeamContainer
import com.example.tournaMake.ui.screens.match.TeamUI
import com.example.tournaMake.ui.screens.match.TeamUIImpl
import com.example.tournaMake.ui.theme.getThemeColors

@Composable
fun TournamentCreationScreen(
    state: ThemeState,
    gamesListLiveData: LiveData<List<Game>>,
    tournamentType: LiveData<List<TournamentType>>,
    mainProfileList:  LiveData<List<MainProfile>>,
    guestProfileList:  LiveData<List<GuestProfile>>,
    navigateToTournament: () -> Unit,
    backFunction: () -> Unit
) {
    BasicScreenWithAppBars(
        state = state,
        backFunction = backFunction,
        showTopBar = true,
        showBottomBar = false
    ) {
        val colorConstants = getThemeColors(themeState = state)
        val imageLogoId =
            if (state.theme == ThemeEnum.Dark) R.drawable.light_writings else R.drawable.dark_writings
        var teamsSet by remember { mutableStateOf(setOf<TeamUI>()) }
        val gamesList = gamesListLiveData.observeAsState()
        val tournamentTypeList = tournamentType.observeAsState()
        val mainProfileListt = mainProfileList.observeAsState()
        val guestProfileListt = guestProfileList.observeAsState()
        Column(
            modifier = Modifier.fillMaxSize(),
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
            Column(
                modifier = Modifier
                    .fillMaxSize(0.9f)
                    .background(color = MaterialTheme.colorScheme.secondary)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,

            ) {
                SelectionMenuGame(gamesList)
                SelectionMenuTournamentType(tournamentTypeList)
                TeamContainer(teamsSet = teamsSet,
                    mainProfileList = mainProfileListt.value ?: emptyList(),
                    guestProfileList = guestProfileListt.value ?: emptyList()
                )
                Button(
                    onClick = { teamsSet = addElement(teamsSet, TeamUIImpl(emptySet(), emptySet(), "")) },
                    modifier = Modifier
                        .background(colorConstants.getButtonBackground())
                        .fillMaxWidth(0.9f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Text("Add team")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navigateToTournament() },
                    modifier = Modifier
                        .background(colorConstants.getButtonBackground())
                        .fillMaxWidth(0.9f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Text("Start tournament")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionMenuGame(list: State<List<Game>?>) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(list.value?.get(0)) }

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
            modifier = Modifier.align(Alignment.Center)
        ) {
            TextField(
                value = selectedText?.name ?: "",
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
                        text = "Select Game",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
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
fun SelectionMenuTournamentType(list: State<List<TournamentType>?>) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(list.value?.get(0)) }

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
            modifier = Modifier.align(Alignment.Center)
        ) {
            TextField(
                value = selectedText?.name ?: "",
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
                        text = "Select tournament type",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
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

fun addElement(set: Set<TeamUI>, team: TeamUI): Set<TeamUI> {
    return setOf(set, setOf(team)).flatten().toSet()
}