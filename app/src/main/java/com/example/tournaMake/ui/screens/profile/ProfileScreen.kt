package com.example.tournaMake.ui.screens.profile

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import com.example.tournaMake.R
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme

/**
 * The screen seen when clicking on a specific profile.
 * */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    state: ThemeState,
    //profile: MainProfile?,
    profileLiveData: LiveData<MainProfile?>,
    backButton: () -> Unit,
    navigateToChart : () -> Unit,
    navigateToLastWeek: () -> Unit
) {
    /*
    * This extension function was imported with:
    * implementation ("androidx.compose.runtime:runtime-livedata:1.6.8")
    * */
    val profile = profileLiveData.observeAsState()
    Log.d("DEV", "In ProfileScreen.kt, profile email = ${profile.value?.email}")

    BasicScreenWithTheme(
        state = state
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back button at the top
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { backButton() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                title = { Text(text = "My Profile") }
            )
            Spacer(modifier = Modifier.height(24.dp))
            // Tabs for "Profile Info" and "Player Games"
            var selectedTabIndex by remember { mutableIntStateOf(0) }
            TabRow(selectedTabIndex = selectedTabIndex, modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.05f)) {
                Text("Profile Info", Modifier.clickable { selectedTabIndex = 0 })
                Text("Player Games", Modifier.clickable { selectedTabIndex = 1 })
            }
            // Display content based on selected tab
            when (selectedTabIndex) {
                0 -> {
                    Column(
                        modifier = Modifier.background(MaterialTheme.colorScheme.primary),
                    ) {
                        Row {
                            Image(
                                painter = painterResource(id = R.drawable.no_profile_picture_icon), //TODO: aggiungere foto profilo
                                contentDescription = "Avatar",
                                modifier = Modifier.fillMaxWidth(0.4f).fillMaxHeight(0.2f)
                            )
                            Column {
                                // TODO: revise this code, it's a bit ugly
                                Text(if (profile.value != null) profile.value!!.username else "Loading...", style = androidx.compose.ui.text.TextStyle(fontSize = 50.sp))
                                Text("No location", style = androidx.compose.ui.text.TextStyle(fontSize = 40.sp))
                            }
                        }
                        Row {
                            Box(
                                modifier = Modifier.fillMaxWidth(0.4f).fillMaxHeight(0.2f).border(width = 2.dp, color = MaterialTheme.colorScheme.secondary)
                            ) { Text(if (profile.value != null) (profile.value!!.wonTournamentsNumber.toString() + " won tournaments") else "Loading...", style = androidx.compose.ui.text.TextStyle(fontSize = 20.sp)) }
                            Box(
                                modifier = Modifier.fillMaxWidth(0.4f).fillMaxHeight(0.2f).border(width = 2.dp, color = MaterialTheme.colorScheme.secondary)
                            ) { Text("10 Played Tournaments", style = androidx.compose.ui.text.TextStyle(fontSize = 20.sp)) }
                        } //TODO: aggiungere al db il numero di tornei giocati
                        Row {
                            Button(
                                onClick = { navigateToChart() },
                                modifier = Modifier.fillMaxWidth(0.4f).fillMaxHeight(0.2f).border(width = 2.dp, color = MaterialTheme.colorScheme.secondary)
                            ) { Text("Games played chart", style = androidx.compose.ui.text.TextStyle(fontSize = 20.sp)) }
                            Button(
                                onClick = { navigateToLastWeek() },
                                modifier = Modifier.fillMaxWidth(0.4f).fillMaxHeight(0.2f).border(width = 2.dp, color = MaterialTheme.colorScheme.secondary)
                            ) { Text("Last played match", style = androidx.compose.ui.text.TextStyle(fontSize = 20.sp)) }
                        }
                    }
                }
                1 -> {
                    // Statistics
                    //TODO: added the history of the played matches
                }
            }
        }
    }
}

