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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tournaMake.R
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(state: ThemeState, profile: MainProfile?) {
    if (profile != null) {
        Log.d("DEV", profile.email)
    }
    BasicScreenWithTheme(
        state = state
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back button at the top
            TopAppBar(
                title = { Text(text = "Profile Screen") },
                actions = {
                    IconButton(onClick = { /* Do something when button is clicked */ }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                }
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
                                painter = painterResource(id = R.drawable.dark_knight),
                                contentDescription = "Avatar",
                                modifier = Modifier.fillMaxWidth(0.4f).fillMaxHeight(0.2f)
                            )
                            Column {
                                if (profile != null) {
                                    Text(profile.username, style = androidx.compose.ui.text.TextStyle(fontSize = 50.sp))
                                }
                                Text("No location", style = androidx.compose.ui.text.TextStyle(fontSize = 40.sp))
                            }
                        }
                        Row {
                            Box(
                                modifier = Modifier.fillMaxWidth(0.4f).fillMaxHeight(0.2f).border(width = 2.dp, color = MaterialTheme.colorScheme.secondary)
                            ) { Text("0 Won Tournaments", style = androidx.compose.ui.text.TextStyle(fontSize = 20.sp)) }
                            Box(
                                modifier = Modifier.fillMaxWidth(0.4f).fillMaxHeight(0.2f).border(width = 2.dp, color = MaterialTheme.colorScheme.secondary)
                            ) { Text("10 Played Tournaments", style = androidx.compose.ui.text.TextStyle(fontSize = 20.sp)) }
                        }
                        Row {
                            Box(
                                modifier = Modifier.fillMaxWidth(0.4f).fillMaxHeight(0.2f).border(width = 2.dp, color = MaterialTheme.colorScheme.secondary)
                            ) { Text("0 Won", style = androidx.compose.ui.text.TextStyle(fontSize = 20.sp)) }
                            Box(
                                modifier = Modifier.fillMaxWidth(0.4f).fillMaxHeight(0.2f).border(width = 2.dp, color = MaterialTheme.colorScheme.secondary)
                            ) { Text("0 Won", style = androidx.compose.ui.text.TextStyle(fontSize = 20.sp)) }
                        }
                    }
                }
                1 -> {
                    // Statistics

                }
            }
        }
    }
}
