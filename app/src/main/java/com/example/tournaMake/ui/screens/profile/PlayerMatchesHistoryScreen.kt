package com.example.tournaMake.ui.screens.profile

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.tournaMake.activities.fetchAndUpdateMatch
import com.example.tournaMake.activities.navgraph.NavigationRoute
import com.example.tournaMake.activities.navigateToSpecifiedMatch
import com.example.tournaMake.data.models.AuthenticationViewModel
import com.example.tournaMake.data.models.MatchListViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerMatchesHistoryScreen(
    navController: NavController,
    owner: LifecycleOwner
) {
    val themeViewModel = koinViewModel<ThemeViewModel>()
    val state by themeViewModel.state.collectAsStateWithLifecycle()
    val authenticationViewModel = koinViewModel<AuthenticationViewModel>()
    val loggedEmail = authenticationViewModel.loggedEmail.collectAsStateWithLifecycle()
    val matchListViewModel = koinViewModel<MatchListViewModel>()
    fetchAndUpdateMatch(loggedEmail.value.loggedProfileEmail, matchListViewModel, owner)
    val matchList = matchListViewModel.matchesListLiveData.observeAsState()

    BasicScreenWithTheme(state = state) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(NavigationRoute.ProfileScreen.route) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                title = { Text(text = "My match list") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(1f)
                    .background(MaterialTheme.colorScheme.secondary)
            ) {
                if (matchList.value != null) {
                    items(matchList.value!!) { item ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { navigateToSpecifiedMatch(matchTmID = item.matchTmID,
                                isOver = item.isOver == 1,
                                vm = matchListViewModel,
                                owner = owner,
                                navController = navController) },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(60.dp)
                        ) {
                            Text("Match of ${item.name}")
                        }
                    }
                }
            }
        }
    }
}