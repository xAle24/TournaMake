package com.example.tournaMake.ui.screens.tournament

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.sampledata.Tournament
import com.example.tournaMake.ui.screens.common.BasicScreenWithAppBars
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme
import com.example.tournaMake.ui.theme.getThemeColors

@Composable
fun TournamentListScreen(
    state: ThemeState,
    matchesListLiveData: LiveData<List<Tournament>>,
    navigateToTournamentCreation: () -> Unit,
    backFunction: () -> Unit
) {
    BasicScreenWithAppBars(
        state = state,
        backFunction = backFunction,
        showTopBar = true,
        showBottomBar = false
    ) {
        val tournamentList = matchesListLiveData.observeAsState(emptyList())
        val colorConstants = getThemeColors(themeState = state)
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navigateToTournamentCreation() },
                modifier = Modifier.background(colorConstants.getButtonBackground()).fillMaxWidth(0.9f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Text("Create tournament")
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(1f)
                    .background(MaterialTheme.colorScheme.secondary)
            ) {
                items(tournamentList.value) { item ->
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

    }
}