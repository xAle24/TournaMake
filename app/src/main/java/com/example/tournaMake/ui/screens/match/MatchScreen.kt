package com.example.tournaMake.ui.screens.match

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.tournaMake.R
import com.example.tournaMake.data.constants.MatchResult
import com.example.tournaMake.data.models.MatchViewModel
import com.example.tournaMake.data.models.TeamDataPacket
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.data.repositories.MatchRepository
import com.example.tournaMake.dataStore
import com.example.tournaMake.sampledata.Game
import com.example.tournaMake.sampledata.MatchTM
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme
import com.example.tournaMake.ui.screens.common.RectangleContainer
import com.example.tournaMake.ui.screens.common.TournaMakeTopAppBar
import com.example.tournaMake.ui.screens.registration.createImageRequest
import java.util.UUID

private val spacerHeight = 20.dp

@Composable
fun MatchScreen(
    state: ThemeState,
    gameImage: Uri?,
    vm: MatchViewModel,
    addMatchToFavorites: (String) -> Unit,
    removeMatchFromFavorites: (String) -> Unit,
    backFunction: () -> Unit,
    endMatch: (Map<String, Pair<Int, MatchResult>>, String) -> Unit,
    saveMatch: (Map<String, Pair<Int, MatchResult>>, String) -> Unit,
) {
    val match by vm.match.observeAsState()
    val playedGameLiveData = vm.playedGame.observeAsState()
    val dataPackets by vm.teamDataPackets.observeAsState()
    var winnerDataPackets: List<TeamDataPacket>
    var shouldShowAlertDialog by remember {
        mutableStateOf(false)
    }

    val backButtonIcon =
        if (state.theme == ThemeEnum.Dark) R.drawable.dark_tournamake_triangle_no_outline else R.drawable.light_tournamake_triangle_no_outline
    val topAppBarBackground =
        if (state.theme == ThemeEnum.Dark) R.drawable.dark_topbarbackground else R.drawable.light_topbarbackground

    if (shouldShowAlertDialog && dataPackets != null && match != null) {
        WinnerSelectionAlertDialog(allTeams = dataPackets!!,
            onDismissRequest = { shouldShowAlertDialog = false },
            /*
            * When the match ends, the user should be prompted to insert all the winning
            * teams (we can show a modal dialog with some checkboxes)
            * Create the map necessary to the callback, but remember to modify its pairs
            * values to reflect the actual match result for each team.
            *
            * If the user selects some teams as winners, then all the other teams are losers.
            * If no winner is selected, all the teams will end up with a draw.
            * This is necessary for updating the headings in the match details screen afterwards -
            * it'd be nice to know if a team has got a draw or some other result.
            * */
            processWinners = {
                winnerDataPackets = it
                var map = buildMap(data = dataPackets!!)
                if (winnerDataPackets.isEmpty()) {
                    // There are no winners, so everybody should score a draw
                    map =
                        map.map { entry -> entry.key to Pair(entry.value.first, MatchResult.Draw) }
                            .toMap()
                } else {
                    val teamIDs = winnerDataPackets.map { packet -> packet.teamID }
                    // I didn't manage to use the functional style to map entries to
                    // a new immutable map, so I used this support map.
                    val mutableMap = mutableMapOf<String, Pair<Int, MatchResult>>()
                    map.forEach { entry ->
                        if (teamIDs.contains(entry.key)) {
                            mutableMap[entry.key] = Pair(entry.value.first, MatchResult.Winner)
                        } else {
                            mutableMap[entry.key] = Pair(entry.value.first, entry.value.second)
                        }
                    }
                    map = mutableMap
                }
                endMatch(map, match!!.matchTmID)
            })
    }

    BasicScreenWithTheme(state = state) {
        Scaffold(containerColor = Color.Transparent,
            modifier = Modifier
                .zIndex(45f)
                .fillMaxSize(),
            topBar = {
                TournaMakeTopAppBar(
                    backButtonIcon = backButtonIcon, topAppBarBackground = topAppBarBackground
                ) {
                    backFunction()
                }
            },
            bottomBar = {
                BottomAppBar {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .zIndex(1f)
                    ) {
                        /** SAVE MATCH BUTTON */
                        Button(onClick = {
                            if (dataPackets != null && match != null) {
                                Log.d(
                                    "DEV-MATCH-SCREEN", "Save Button was clicked in Match Screen!"
                                )
                                saveMatch(buildMap(dataPackets!!), match!!.matchTmID)
                            }
                        }) {
                            Text("Save")
                        }
                        Spacer(Modifier.width(16.dp))
                        /** END MATCH BUTTON */
                        Button(onClick = {

                            shouldShowAlertDialog = true
                        }) {
                            Text("End")
                        }
                    }
                }
            }) { paddingValues ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
            ) {
                MatchHeading(
                    gameImage = gameImage,
                    gameName = playedGameLiveData.value?.name ?: "Loading...",
                    match,
                    addMatchToFavorites,
                    removeMatchFromFavorites
                )
                Spacer(Modifier.height(spacerHeight))
                dataPackets?.forEach { team ->
                    TeamElementInMatchScreen(dataPacket = team)
                    Spacer(modifier = Modifier.height(spacerHeight))
                }
                Spacer(Modifier.height(spacerHeight))
            }
        }
    }
}

@Composable
fun WinnerSelectionAlertDialog(
    allTeams: List<TeamDataPacket>,
    onDismissRequest: () -> Unit,
    processWinners: (List<TeamDataPacket>) -> Unit
) {
    var selectedTeams by remember { mutableStateOf<List<TeamDataPacket>>(emptyList()) }
    AlertDialog(onDismissRequest = { /*showDialog = false*/ onDismissRequest() }, title = {
        Text(
            "Select winning teams if any (leaving all teams blank will result in a collective draw)."
        )
    }, text = {
        LazyColumn() {
            items(allTeams) { localTeamDataPacket ->
                var isChecked by remember {
                    mutableStateOf(false)
                }
                Row {
                    Checkbox(checked = isChecked, onCheckedChange = {
                        if (isChecked) {
                            selectedTeams =
                                selectedTeams.filter { team -> team != localTeamDataPacket }
                            isChecked = false
                        } else {
                            selectedTeams =
                                listOf(selectedTeams, listOf(localTeamDataPacket)).flatten()
                            isChecked = true
                        }
                    })
                    Text(localTeamDataPacket.teamUI.getTeamName())
                }
            }
        }
    }, confirmButton = {
        Button(onClick = {
            processWinners(selectedTeams)
        }) {
            Text("Confirm")
        }
    }, dismissButton = {
        Button(onClick = { onDismissRequest() }) {
            Text("Cancel")
        }
    })
}

@Composable
fun MatchHeading(
    gameImage: Uri?,
    gameName: String,
    match: MatchTM?,
    addMatchToFavorites: (String) -> Unit,
    removeMatchFromFavorites: (String) -> Unit
) {
    var isFavorite by remember { mutableStateOf(match?.favorites == 1) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max) // set as high as the highest child
    ) {
        RectangleContainer(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                .fillMaxWidth(0.9f)
                .align(Alignment.Center)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround
            ) {
                if (gameImage != null) {
                    AsyncImage(
                        model = gameImage, contentDescription = "Game image"
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.no_game_picture),
                        contentDescription = "No game image found",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(start = 10.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Match", style = MaterialTheme.typography.headlineSmall)
                    Text(gameName)
                }
                IconButton(
                    onClick = {
                        isFavorite = if (!isFavorite) {
                            if (match != null) {
                                addMatchToFavorites(match.matchTmID)
                            }
                            true
                        } else {
                            if (match != null) {
                                removeMatchFromFavorites(match.matchTmID)
                            }
                            false
                        }
                    },
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favourite Match indicator",
                    )
                }
            }
        }
    }
}

@Composable
fun TeamElementInMatchScreen(
    dataPacket: TeamDataPacket
) {
    var score by remember { mutableStateOf(TextFieldValue(dataPacket.teamScore.toString())) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max) // set as high as the highest child
    ) {
        RectangleContainer(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f))
                .fillMaxWidth(0.9f)
                .align(Alignment.Center)
        ) {
            Text(
                dataPacket.teamUI.getTeamName(),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 10.dp)
            )
            HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.onSecondary)
            Spacer(modifier = Modifier.height(spacerHeight))
            LazyRow(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                items(dataPacket.teamUI.getGuestProfiles().toList()) {
                    MiniProfileImage(profileName = it.username)
                }
                items(dataPacket.teamUI.getMainProfiles().toList()) {
                    MiniProfileImage(
                        profileImage = it.profileImage?.toUri(), profileName = it.username
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Current Score",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth(),
                    //.background(Color.White),
                    textAlign = TextAlign.Center
                )

                TextField(
                    value = score, onValueChange = { score = it }, modifier = Modifier,
                    //.align(Alignment.Center)
                    trailingIcon = {
                        Icon(
                            Icons.Filled.Edit, contentDescription = null
                        )
                    }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Spacer(modifier = Modifier.height(spacerHeight))
        }
    }
}

@Composable
fun MiniProfileImage(
    profileImage: Uri? = null, profileName: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedCard(
            modifier = Modifier
                //.background(Color.White)
                .width(80.dp)
                .height(80.dp)
                .padding(4.dp),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(width = 2.dp, MaterialTheme.colorScheme.outline)
        ) {
            // Load your image here using imageResId
            // Example: Image(painter = painterResource(id = imageResId), contentDescription = null)
            // Text(username, modifier = Modifier.padding(4.dp))
            if (profileImage == null) {
                Image(
                    painterResource(id = R.drawable.no_profile_picture_icon),
                    contentDescription = null
                )
            } else {
                AsyncImage(
                    model = createImageRequest(LocalContext.current, profileImage),
                    contentDescription = null
                )
            }
        }
        Text(profileName)
    }
    Spacer(modifier = Modifier.width(8.dp))
}

@Preview
@Composable
fun MyMatchScreenPreview() {
    val vm = MatchViewModel(MatchRepository(LocalContext.current.dataStore))
    vm.changeTeamDataPackets(
        listOf(
            TeamDataPacket(testTeam1, 200, "team1ID", false),
            TeamDataPacket(testTeam2, 100, "team2ID", false)
        )
    )
    MatchScreen(state = ThemeState(ThemeEnum.Light),
        gameImage = null,
        vm = vm,
        addMatchToFavorites = {},
        removeMatchFromFavorites = {},
        saveMatch = fun(_: Map<String, Pair<Int, MatchResult>>, _: String) {},
        endMatch = fun(_: Map<String, Pair<Int, MatchResult>>, _: String) {},
        backFunction = {})
}

/**
 * Warning: builds a map with the default match result of "Loser" for everybody.
 * */
fun buildMap(data: List<TeamDataPacket>): Map<String, Pair<Int, MatchResult>> {
    return data.associate { dataPacket ->
        dataPacket.teamID to Pair(
            dataPacket.teamScore, MatchResult.Loser
        )
    }
}
