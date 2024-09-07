package com.example.tournaMake.ui.screens.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.tournaMake.data.models.GameDetailsViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.ui.screens.common.BasicScreenWithAppBars
import org.koin.androidx.compose.koinViewModel

@Composable
fun GameDetailsScreen(
    navController: NavController,
    owner: LifecycleOwner
) {
    val themeViewModel = koinViewModel<ThemeViewModel>()
    val state by themeViewModel.state.collectAsStateWithLifecycle()
    val vm = koinViewModel<GameDetailsViewModel>()
    val gameDetails by vm.gameDetailsListLiveData.observeAsState()

    BasicScreenWithAppBars(
        state = state,
        backFunction = { navController.navigateUp() },
        showTopBar = true,
        showBottomBar = false
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            //TODO add games images
            Text(text = gameDetails?.name ?: "Loading..")
        }
    }
}