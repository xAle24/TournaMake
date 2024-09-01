package com.example.tournaMake.ui.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.example.tournaMake.data.models.GamesListViewModel
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.sampledata.Game
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme
import java.util.UUID
import kotlin.reflect.KFunction1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesListScreen(
    state: ThemeState,
    gamesListLiveData: LiveData<List<Game>>,
    addGame: KFunction1<Game, Unit>,
    backButton: () -> Unit,
    recreationFunction: () -> Unit
) {
    val gameList = gamesListLiveData.observeAsState()
    var showDialog by remember { mutableStateOf(false) }

    BasicScreenWithTheme(state = state) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { backButton() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                title = { Text(text = "Game list") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(80.dp)
            ) {
                Text("Add a game")
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(1f)
                    .background(MaterialTheme.colorScheme.secondary)
            ) {
                if (gameList.value != null) {
                    items(gameList.value!!) { item ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { /* Do something when button is clicked */ },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(60.dp)
                        ) {
                            Text(item.name)
                        }
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
                                favorites = 0 // TODO: change to actual value
                            )
                            addGame(game)
                            showDialog = false
                            recreationFunction() // to update the UI... ugly but we can't do otherwise
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
