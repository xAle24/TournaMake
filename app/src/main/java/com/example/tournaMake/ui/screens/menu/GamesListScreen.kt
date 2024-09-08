package com.example.tournaMake.ui.screens.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.tournaMake.R
import com.example.tournaMake.activities.addGame
import com.example.tournaMake.activities.addGameToFavorites
import com.example.tournaMake.activities.navgraph.NavigationRoute
import com.example.tournaMake.activities.removeGameFromFavorites
import com.example.tournaMake.data.models.GamesListViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.sampledata.Game
import com.example.tournaMake.ui.screens.common.BasicScreenWithAppBars
import com.example.tournaMake.ui.theme.getThemeColors
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

@Composable
fun GamesListScreen(
    navController: NavController,
    owner: LifecycleOwner
) {
    val themeViewModel = koinViewModel<ThemeViewModel>()
    val state by themeViewModel.state.collectAsStateWithLifecycle()
    val colorConstants = getThemeColors(themeState = state)
    // View Model of profile list
    val gamesListViewModel = koinViewModel<GamesListViewModel>()
    val gameList = gamesListViewModel.gamesListLiveData.observeAsState()
    var showDialog by remember { mutableStateOf(false) }

    BasicScreenWithAppBars(
        state = state,
        backFunction = { navController.navigate(NavigationRoute.MenuScreen.route) },
        showTopBar = true,
        showBottomBar = false
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { showDialog = true },
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
                Text("Add a game")
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(1f)
            ) {
                if (gameList.value != null) {
                    items(gameList.value!!) { item ->
                        GameCard(game = item, navController = navController, owner = owner)
                    }
                }
            }
            if (showDialog) {
                val gameID by remember { mutableStateOf(UUID.randomUUID().toString()) }
                var name by remember { mutableStateOf("") }
                var description by remember { mutableStateOf("") }
                var duration by remember { mutableStateOf("") }
                var minPlayer by remember { mutableStateOf("") }
                var maxPlayers by remember { mutableStateOf("") }

                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Add a game") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Name") }
                            )
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text("Description") }
                            )
                            OutlinedTextField(
                                value = duration,
                                onValueChange = { duration = it },
                                label = { Text("Duration") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            OutlinedTextField(
                                value = minPlayer,
                                onValueChange = { minPlayer = it },
                                label = { Text("Minimum Players") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            OutlinedTextField(
                                value = maxPlayers,
                                onValueChange = { maxPlayers = it },
                                label = { Text("Maximum Players") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            val game = Game(
                                gameID = gameID,
                                name = name,
                                description = description,
                                duration = duration.toInt(),
                                minPlayers = minPlayer.toInt(),
                                maxPlayers = maxPlayers.toInt(),
                                favorites = 0
                            )
                            addGame(game, owner)
                            showDialog = false
                        }) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}
@Composable
fun GameCard(
    game: Game,
    navController: NavController,
    owner: LifecycleOwner
) {
    var isFavorite by remember { mutableStateOf(game.favorites == 1) }
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { /*TODO: create screen for details of game*/ },
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
            disabledContentColor = MaterialTheme.colorScheme.error
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.no_game_picture),
                contentDescription = "Appropriate game image",
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .padding(20.dp, 20.dp)
                    .clip(RoundedCornerShape(20))
            )
            Text(
                game.name,
                modifier = Modifier.padding(0.dp, 10.dp),
                style = MaterialTheme.typography.displaySmall
            )
            IconButton(
                onClick = {
                    isFavorite = if (!isFavorite) {
                        addGameToFavorites(game.gameID, owner)
                        true
                    } else {
                        removeGameFromFavorites(game.gameID, owner)
                        false
                    }
                },
                modifier = Modifier.size(78.dp) // Adjust the size as needed
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp)
                )
            }
        }
    }
}