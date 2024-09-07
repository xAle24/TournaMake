package com.example.tournaMake.ui.screens.tournament

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.tournaMake.R
import com.example.tournaMake.activities.fetchAndUpdateTournament
import com.example.tournaMake.activities.navgraph.NavigationRoute
import com.example.tournaMake.activities.navigateToSpecifiedMatch
import com.example.tournaMake.data.constants.mapIntegerToMatchStatus
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.data.models.TournamentIDViewModel
import com.example.tournaMake.data.models.TournamentListViewModel
import com.example.tournaMake.sampledata.Tournament
import com.example.tournaMake.ui.screens.common.BasicScreenWithAppBars
import com.example.tournaMake.ui.screens.match.CreateMatchButton
import com.example.tournaMake.ui.screens.match.FilterButton
import com.example.tournaMake.ui.theme.ColorConstants
import com.example.tournaMake.ui.theme.getThemeColors
import kotlinx.coroutines.runBlocking
import org.koin.androidx.compose.koinViewModel

@Composable
fun TournamentListScreen(
    navController: NavController,
    owner: LifecycleOwner
) {
    val themeViewModel = koinViewModel<ThemeViewModel>()
    val state by themeViewModel.state.collectAsStateWithLifecycle()
    val tournamentListViewModel = koinViewModel<TournamentListViewModel>()
    val selectedTournamentIDViewModel = koinViewModel<TournamentIDViewModel>()
    fetchAndUpdateTournament(tournamentListViewModel, owner)
    BasicScreenWithAppBars(
        state = state,
        backFunction = { navController.navigateUp() },
        showTopBar = true,
        showBottomBar = false
    ) {
        val tournamentList =
            tournamentListViewModel.tournamentListLiveData.observeAsState(emptyList())
        val colorConstants = getThemeColors(themeState = state)
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                CreateTournamentButton(
                    { navController.navigate(NavigationRoute.TournamentCreationScreen.route) },
                    colorConstants,
                    Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(5.dp))
                TournamentFilterButton(
                    colorConstants,
                    Modifier.align(Alignment.CenterVertically),
                    onClick = { /*showDialog = true*/ /*TODO*/ }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(1f)
            ) {
                items(tournamentList.value) { item ->
                    Spacer(modifier = Modifier.height(16.dp))
                    TournamentCard(
                        onClick = {
                            /* Trying to ensure that writing the repository is completed before moving to next screen */
                            runBlocking { selectedTournamentIDViewModel.saveTournamentIDInPreferences(item.tournamentID) }
                            navController.navigate("${NavigationRoute.TournamentScreen.route}?refresh=true")
                        },
                        tournament = item
                    )
                }
            }
        }
    }
}

@Composable
fun CreateTournamentButton(
    onClick: () -> Unit,
    colorConstants: ColorConstants,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .height(60.dp)
            .fillMaxWidth(0.67f)
            .background(colorConstants.getButtonBackground()),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(3.dp, MaterialTheme.colorScheme.tertiary)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.triangleicon),
                contentDescription = "Create Tournament",
                modifier = Modifier.size(40.dp),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "Create Tournament",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
@Composable
fun TournamentFilterButton(
    colorConstants: ColorConstants,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .width(80.dp)
            .clip(RoundedCornerShape(30.dp))
            .height(60.dp)
            .background(colorConstants.getButtonBackground()),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(3.dp, MaterialTheme.colorScheme.tertiary)
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.filter),
                contentDescription = "Filter",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(20.dp)
            )
            Text("Filter", style = MaterialTheme.typography.bodySmall)
        }
    }
}


@Composable
fun TournamentCard(
    onClick: () -> Unit,
    tournament: Tournament
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        border = BorderStroke(3.dp, MaterialTheme.colorScheme.tertiary)
    ) {
        Column {
            Text(style = MaterialTheme.typography.headlineSmall, text = tournament.name, modifier = Modifier.padding(10.dp))
            Text("Status: ${if (tournament.isOver == 1) "Ended" else "Ongoing"}", modifier = Modifier.padding(10.dp))
        }
    }
}