package com.example.tournaMake.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.tournaMake.activities.fetchAndUpdateGraph
import com.example.tournaMake.activities.navgraph.NavigationRoute
import com.example.tournaMake.data.models.AuthenticationViewModel
import com.example.tournaMake.data.models.GraphViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.sampledata.MatchTM
import com.example.tournaMake.sampledata.PlayedGame
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme
import com.hd.charts.StackedBarChartView
import com.hd.charts.common.model.MultiChartDataSet
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen(
    navController: NavController,
    owner: LifecycleOwner
) {
    // See ThemeViewModel.kt
    val themeViewModel = koinViewModel<ThemeViewModel>()
    // The following line converts the StateFlow contained in the ViewModel
    // to a State object. State objects can trigger recompositions, while
    // StateFlow objects can't. The 'withLifecycle' part ensures this state
    // is destroyed when we leave this Activity.
    val state by themeViewModel.state.collectAsStateWithLifecycle()
    val authenticationViewModel = koinViewModel<AuthenticationViewModel>()
    val loggedEmail = authenticationViewModel.loggedEmail.collectAsStateWithLifecycle()
    val graphViewModel = koinViewModel<GraphViewModel>()
    val gameObserver = Observer<List<PlayedGame>?> {}
    val matchObserver = Observer<List<MatchTM>?> {}
    graphViewModel.gamesListLiveData.observe(owner, gameObserver)
    graphViewModel.matchListLiveData.observe(owner, matchObserver)
    val gamesLiveData = graphViewModel.gamesListLiveData

    fetchAndUpdateGraph(loggedEmail.value.loggedProfileEmail, graphViewModel, owner)
    val gamesData = gamesLiveData.observeAsState(listOf())

    val items = gamesData.value
        .map { playedGame -> playedGame.name to listOf(playedGame.timesPlayed.toFloat()) }
        .toList()
    println("In Chart Screen, about to print list values. ")
    items.forEach { it -> println("Element: $it") }
    val dataSet =
        MultiChartDataSet(
            items = items.ifEmpty { listOf<Pair<String, List<Float>>>("No value" to listOf(0.0f)) },
            prefix = "",
            categories = listOf("Times Played"),
            title = "Games"
        )

    BasicScreenWithTheme(
        state = state
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            // Back button at the top
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(NavigationRoute.ProfileScreen.route) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                title = { Text(text = "My Profile") }
            )
            Spacer(modifier = Modifier.height(24.dp))
            /*BarChartView( //old graph with single bar, (è più adatto al nostro contesto)
                dataSet = ChartDataSet(
                items = listOf(
                    if (list.isNotEmpty()) list[0].times_played.toFloat() else 1.15f,
                    50f
                ),
                    title = "Games"
                )
            )*/

            /*
            * Use the key() function to tell Compose Runtime the value used to identify this
            * part of the tree. Documentation:
            * https://developer.android.com/develop/ui/compose/lifecycle#key
            * */
            key(dataSet) {
                CustomBarChart(data = dataSet)
            }
            Spacer(modifier = Modifier.height(50.dp))
            //Text("Games played: ${gamesData.value}")
        }
    }
}

@Composable
fun CustomBarChart(
    data: MultiChartDataSet
) {
    StackedBarChartView(dataSet = data)
}
