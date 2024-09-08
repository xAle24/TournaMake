package com.example.tournaMake.ui.screens.match

import Converters.fromTimestamp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.tournaMake.R
import com.example.tournaMake.activities.addMatchToFavorites
import com.example.tournaMake.activities.navgraph.NavigationRoute
import com.example.tournaMake.activities.navigateToSpecifiedMatch
import com.example.tournaMake.activities.removeMatchFromFavorites
import com.example.tournaMake.data.constants.mapIntegerToMatchStatus
import com.example.tournaMake.data.models.MatchListViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.sampledata.MatchGameData
import com.example.tournaMake.ui.screens.common.BasicScreenWithAppBars
import com.example.tournaMake.ui.theme.ColorConstants
import com.example.tournaMake.ui.theme.getThemeColors
import com.example.tournaMake.utils.Searchbar
import org.koin.androidx.compose.koinViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun MatchListScreen(
    navController: NavController,
    owner: LifecycleOwner,
) {
    val themeViewModel = koinViewModel<ThemeViewModel>()
    val state by themeViewModel.state.collectAsStateWithLifecycle()
    val matchListViewModel = koinViewModel<MatchListViewModel>()
    // Data being fetched from database
    val matchesList = matchListViewModel.allMatchesListLiveData.observeAsState(emptyList())
    val colorConstants = getThemeColors(themeState = state)
    val searchbar2 = Searchbar(matchesList.value)
    var filteredEntries  = searchbar2.getFilteredEntries()
    var showDialog by remember { mutableStateOf(false) }
    var selectedPredicate by remember { mutableStateOf<(MatchGameData) -> Boolean>({ true }) }
    val options = listOf("All", "Favorites", "Completed", "Ongoing")
    val selectedOption = remember { mutableStateOf(options[0]) }
    BasicScreenWithAppBars(
        state = state,
        backFunction = { navController.navigateUp() },
        showTopBar = true,
        showBottomBar = false
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                CreateMatchButton(
                    { navController.navigate(NavigationRoute.MatchCreationScreen.route) },
                    colorConstants,
                    Modifier.align(Alignment.CenterVertically)
                )
                FilterButton(
                    colorConstants,
                    Modifier.align(Alignment.CenterVertically),
                    onClick = { showDialog = true }
                )
            }
            key(filteredEntries){
                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                ) {
                    items(filteredEntries) { item ->
                        MatchCard(match = item,
                            vm = matchListViewModel,
                            navController = navController,
                            owner = owner)
                    }
                }
            }
        }
        if (showDialog) {
            FilterDialog(
                onDismiss = { showDialog = false },
                options = options,
                selectedOption = selectedOption,
                onPredicateSelected = { predicate ->
                    selectedPredicate = predicate
                    searchbar2.filterEntries(predicate)
                    filteredEntries = searchbar2.getFilteredEntries()
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun CreateMatchButton(
    onClick: () -> Unit,
    colorConstants: ColorConstants,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .height(60.dp)
            .fillMaxWidth(0.6f)
            .background(colorConstants.getButtonBackground()),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(3.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.d20),
                contentDescription = "Create Match",
                modifier = Modifier.size(40.dp),
            )
            Spacer(Modifier.width(5.dp))
            Text(
                "Create Match",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun FilterButton(
    colorConstants: ColorConstants,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(0.55f)
            .clip(RoundedCornerShape(30.dp))
            .height(60.dp)
            .background(colorConstants.getButtonBackground()),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(3.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.filter),
                contentDescription = "Filter",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text("Filter")
        }
    }
}

@Composable
fun MatchCard(
    match: MatchGameData,
    vm: MatchListViewModel,
    navController: NavController,
    owner: LifecycleOwner
) {
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
                    vm = vm,
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
        Column(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Content(match = match)
        }
    }
}

@Composable
fun DescriptionText(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(5.dp, 5.dp)
    )
}

@Composable
fun Content(
    match: MatchGameData
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Image(
            painter = painterResource(id = R.drawable.no_game_picture),
            contentDescription = "Appropriate game image",
            modifier = Modifier
                .padding(30.dp, 30.dp)
                .clip(RoundedCornerShape(20))
        )
        Column(
            modifier = Modifier
                .padding(0.dp, 30.dp)
        ) {
            val dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy")
            val date = fromTimestamp(match.date)
                ?.toInstant()
                ?.atZone(ZoneId.systemDefault())
                ?.toLocalDate()
            val stringToPrint = date?.format(dateTimeFormatter)
            DescriptionText(text = "Date: ${stringToPrint ?: fromTimestamp(match.date).toString()}")
            DescriptionText(text = "Status: ${mapIntegerToMatchStatus(match.isOver)}")
        }
    }
}
@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    options: List<String>,
    selectedOption: MutableState<String>,
    onPredicateSelected: (predicate: (MatchGameData) -> Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Filter") },
        text = {
            Column {
                options.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedOption.value == option),
                            onClick = { selectedOption.value = option },
                            colors = RadioButtonDefaults.colors(
                                unselectedColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(option, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val predicate: (MatchGameData) -> Boolean = when (selectedOption.value) {
                        "Favorites" -> { match -> match.favorites == 1 }
                        "Completed" -> { match -> match.isOver == 1 }
                        "Ongoing" -> { match -> match.isOver == 0 }
                        else -> { _ -> true }
                    }
                    onPredicateSelected(predicate)
                }
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}