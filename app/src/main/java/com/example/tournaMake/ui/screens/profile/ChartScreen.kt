package com.example.tournaMake.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.example.tournaMake.R
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.sampledata.PlayedGame
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme
import com.hd.charts.BarChartView
import com.hd.charts.StackedBarChartView
import com.hd.charts.common.model.ChartDataSet
import com.hd.charts.common.model.MultiChartDataSet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen(
    state: ThemeState,
    gamesLiveData: LiveData<List<PlayedGame>>,
    backButton: () -> Unit
) {
    val gamesData by gamesLiveData.observeAsState()
    val list = gamesData?.toList() ?: emptyList()

    val items = listOf(
        list[0].name to listOf(list[0].times_played.toFloat(), 8810.34f, 30000.57f),
        "Strawberry Mall" to listOf(8261.68f, 8810.34f, 30000.57f),
        "Lime Av." to listOf(1500.87f, 2765.58f, 33245.81f),
        "Apple Rd." to listOf(5444.87f, 233.58f, 67544.81f)
    )

    val dataSet = MultiChartDataSet(
        items = items,
        prefix = "$",
        categories = listOf(list[0].name, "Feb", "Mar"),
        title = "Games"
    )
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
            BarChartView( //old graph with single bar, (è più giusto al nostro contesto)
                dataSet = ChartDataSet(
                items = listOf(
                    list[0].times_played.toFloat()
                ),
                    title = "Games"
                )
            )
            //StackedBarChartView(dataSet = dataSet)
        }
    }
}
