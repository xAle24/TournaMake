package com.example.tournaMake.ui.screens.profile

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.tournaMake.activities.addMatchToFavorites
import com.example.tournaMake.activities.fetchAndUpdateMatch
import com.example.tournaMake.activities.navigateToSpecifiedMatch
import com.example.tournaMake.activities.removeMatchFromFavorites
import com.example.tournaMake.data.models.AuthenticationViewModel
import com.example.tournaMake.data.models.MatchListViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.ui.screens.common.BasicScreenWithAppBars
import org.koin.androidx.compose.koinViewModel

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
    fetchAndUpdateMatch(loggedEmail.value.loggedProfileEmail, matchListViewModel)
    val matchList = matchListViewModel.loggedPlayerMatchesHistory.observeAsState()

    BasicScreenWithAppBars(
        state = state,
        backFunction = { navController.navigateUp() },
        showTopBar = true,
        showBottomBar = true
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(1f)
            ) {
                if (matchList.value != null) {
                    items(matchList.value!!) { match ->
                        var isFavorite by remember { mutableStateOf(match.favorites == 1) }
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clickable {
                                    navigateToSpecifiedMatch(
                                        matchTmID = match.matchTmID,
                                        isOver = match.isOver == 1,
                                        vm = matchListViewModel,
                                        owner = owner,
                                        navController = navController
                                    )
                                },
                            colors = CardColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                                disabledContentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 15.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    match.name + " match",
                                    modifier = Modifier.padding(0.dp, 10.dp),
                                    style = MaterialTheme.typography.displaySmall
                                )
                                IconButton(
                                    onClick = {
                                        isFavorite = if (!isFavorite) {
                                            addMatchToFavorites(match.matchTmID, owner)
                                            true
                                        } else {
                                            removeMatchFromFavorites(match.matchTmID, owner)
                                            false
                                        }
                                    },
                                    modifier = Modifier.size(78.dp) // Adjust the size as needed
                                ) {
                                    Icon(
                                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .padding(end = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}