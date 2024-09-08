package com.example.tournaMake.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.tournaMake.activities.fetchAndUpdateGraph
import com.example.tournaMake.data.models.AuthenticationViewModel
import com.example.tournaMake.data.models.GraphViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.ui.screens.common.BasicScreenWithAppBars
import com.hd.charts.StackedBarChartView
import com.hd.charts.common.model.MultiChartDataSet
import okhttp3.internal.toImmutableList
import org.koin.androidx.compose.koinViewModel

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
    val gamesLiveData = graphViewModel.gamesListLiveData
    fetchAndUpdateGraph(loggedEmail.value.loggedProfileEmail, graphViewModel, owner)
    val gamesData = gamesLiveData.observeAsState(listOf())
    val items = generateSequence(0) { it + 1 }
        .take(gamesData.value.size)
        .map{index -> index to gamesData.value[index]}
        .map{indexAndPlayedGamePair ->
            val floatsList = MutableList(gamesData.value.size) {0.0f}
            floatsList[indexAndPlayedGamePair.first] = indexAndPlayedGamePair.second.timesPlayed.toFloat()
            return@map indexAndPlayedGamePair.second.name to floatsList.toImmutableList()
        }.toList()
    val dataSet =
        MultiChartDataSet(
            items = items.ifEmpty { listOf("No value" to listOf(0.0f)) },
            prefix = "",
            categories = items.map { it.first },
            title = "Games"
        )

    BasicScreenWithAppBars(
        state = state,
        backFunction = { navController.navigateUp() },
        showTopBar = true,
        showBottomBar = false
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            /*
            * Use the key() function to tell Compose Runtime the value used to identify this
            * part of the tree. Documentation:
            * https://developer.android.com/develop/ui/compose/lifecycle#key
            * */
            key(dataSet) {
                CustomBarChart(data = dataSet)
            }
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun CustomBarChart(
    data: MultiChartDataSet
) {
    StackedBarChartView(dataSet = data)
}
