package com.example.tournaMake.ui.screens.match

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.tournaMake.R
import com.example.tournaMake.activities.createMatch
import com.example.tournaMake.activities.fetchDataForMatchCreation
import com.example.tournaMake.data.models.GuestProfileListViewModel
import com.example.tournaMake.data.models.MatchCreationViewModel
import com.example.tournaMake.data.models.MatchViewModel
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.sampledata.Game
import com.example.tournaMake.ui.screens.common.BasicScreenWithAppBars
import com.example.tournaMake.ui.screens.common.RectangleContainer
import com.example.tournaMake.ui.theme.getThemeColors
import org.koin.androidx.compose.koinViewModel

@Composable
fun MatchCreationScreen(
    navController: NavController,
    owner: LifecycleOwner,
) {
    val themeViewModel = koinViewModel<ThemeViewModel>()
    val state by themeViewModel.state.collectAsStateWithLifecycle()
    val matchCreationViewModel = koinViewModel<MatchCreationViewModel>()
    fetchDataForMatchCreation(matchCreationViewModel, owner)
    val matchViewModel = koinViewModel<MatchViewModel>()
    val gamesListLiveData = matchCreationViewModel.games
    val imageLogoId =
        if (state.theme == ThemeEnum.Dark) R.drawable.light_writings else R.drawable.dark_writings
    val context = LocalContext.current

    BasicScreenWithAppBars(
        state = state,
        backFunction = { navController.navigateUp() },
        showTopBar = true,
        showBottomBar = false
    ) {
        Column {
            Logo(imageLogoId)
            Spacer(Modifier.height(30.dp))
            RectangleContainer(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .align(Alignment.CenterHorizontally)
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f))
            ) {
                var selectedGame: Game? by remember {
                    mutableStateOf(null)
                }
                SelectionMenu(gamesListLiveData) { selectedGame = it }

                TeamContainer(
                    removeTeam = matchCreationViewModel::removeTeam
                )

                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    BottomTeamScreenButton(
                        state = state,
                        //.fillMaxWidth(0.3f)
                        /**
                         * This is the function that calls the addTeam() callback,
                         * creating a new empty team.
                         * */
                        onClick = { matchCreationViewModel.addTeam(TeamUIImpl(emptySet(), emptySet(), "")) },
                        text = "Add Team"
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    BottomTeamScreenButton(
                        state = state,
                        iconEnabled = false,
                        text = "Create Match",
                        onClick = {
                            if (selectedGame == null) {
                                Toast.makeText(context, "Must choose a game first!", Toast.LENGTH_SHORT).show()
                            } else if (matchCreationViewModel.teamsSet.value != null && matchCreationViewModel.teamsSet.value!!.size < 2){
                                Toast.makeText(context, "At least 2 teams must be present!", Toast.LENGTH_SHORT).show()
                            } else if (matchCreationViewModel.teamsSet.value != null) {
                                createMatch(
                                    selectedGame!!.gameID,
                                    matchCreationViewModel,
                                    matchViewModel,
                                    owner,
                                    navController
                                )
                            } else {
                                Toast.makeText(context, "Unexpected error: teamsSet is null!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomTeamScreenButton(
    state: ThemeState,
    onClick: () -> Unit = {},
    iconEnabled: Boolean = true,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxHeight()
            .clickable { onClick() }
            .clip(RoundedCornerShape(6.dp))
            .background(getThemeColors(themeState = state).getButtonBackground())
    ) {
        if (iconEnabled) {
            Icon(
                Icons.Filled.Add,
                null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .width(25.dp)
            )
        }
        //Spacer(modifier = Modifier.width(10.dp))
        Text(
            text,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(10.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun Logo(
    imageLogoId: Int
) {
    Image(
        painter = painterResource(id = imageLogoId),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .size(100.dp)
        //.background(Color.Black)
    )
}

/**
 * Selection dropdown menu for games
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionMenu(
    gamesList: LiveData<List<Game>>,
    updateCurrentlySelectedGame: (Game?) -> Unit
) {
    val gamesListLiveData = gamesList.observeAsState()
    val gamesNames = gamesListLiveData.value
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("No game selected") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            //.background(Color.Black)
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
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
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
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                textStyle = MaterialTheme.typography.headlineLarge
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
            ) {
                gamesNames?.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item.name) },
                        onClick = {
                            selectedText = item.name
                            expanded = false
                            updateCurrentlySelectedGame(item)
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

/*
@Preview
@Composable
fun PreviewMatchCreationScreen() {
    MatchCreationScreen(
        state = ThemeState(ThemeEnum.Light),
        backFunction = {},
        {},
        liveData { emptyList<Game>() },
    )
}*/
