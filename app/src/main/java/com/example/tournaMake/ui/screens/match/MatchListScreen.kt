package com.example.tournaMake.ui.screens.match

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.LiveData
import com.example.tournaMake.R
import com.example.tournaMake.data.constants.mapIntegerToMatchStatus
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.sampledata.MatchGameData
import com.example.tournaMake.ui.screens.common.BasicScreenWithAppBars
import com.example.tournaMake.ui.theme.ColorConstants
import com.example.tournaMake.ui.theme.getThemeColors
import com.example.tournaMake.utils.Searchbar
import kotlin.reflect.KFunction1

@Composable
fun MatchListScreen(
    state: ThemeState,
    matchesListLiveData: LiveData<List<MatchGameData>>,
    searchbar: Searchbar<MatchGameData>,
    navigationFunction: () -> Unit,
    addFavoritesFunction: KFunction1<String, Unit>,
    removeFavoritesFunction: KFunction1<String, Unit>,
    backFunction: () -> Unit
) {
    // Data being fetched from database
    val matchesList = matchesListLiveData.observeAsState(emptyList())
    val colorConstants = getThemeColors(themeState = state)
    val filteredEntries by remember { mutableStateOf(searchbar.getFilteredEntries()) }
    BasicScreenWithAppBars(
        state = state,
        backFunction = backFunction,
        showTopBar = true,
        showBottomBar = false
    ) {
        Column {
            Row(
                /*TODO remove sto cazzo di spazio che mette a caso tra se e la barra superiore*/
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                CreateMatchButton(
                    navigationFunction,
                    colorConstants,
                    Modifier.align(Alignment.CenterVertically)
                )
                FilterButton(
                    colorConstants,
                    Modifier.align(Alignment.CenterVertically)
                )
            }
            key(filteredEntries){
                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                ) {
                    items(filteredEntries) { item ->
                        MatchCard(match = item, addFavoritesFunction, removeFavoritesFunction)
                    }
                }
            }
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
            .padding(0.dp, 40.dp, 0.dp, 4.dp)
            .clip(RoundedCornerShape(30.dp))
            .height(60.dp)
            .fillMaxWidth(0.6f)
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
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { /* TODO: implement filters */ },
        modifier = modifier
            .padding(0.dp, 40.dp, 0.dp, 4.dp)
            .fillMaxWidth(0.55f)
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
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text("Filter")
        }
    }
}

@Composable
fun MatchCard(
    match: MatchGameData,
    addToFavoritesFunction: KFunction1<String, Unit>,
    removeFavoritesFunction: KFunction1<String, Unit>,
) {
    var isFavorite by remember { mutableStateOf(match.favorites == 1) }
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
            disabledContentColor = MaterialTheme.colorScheme.error
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                        addToFavoritesFunction(match.matchTmID)
                        true
                    } else {
                        removeFavoritesFunction(match.matchTmID)
                        false
                    }
                },
                modifier = Modifier.size(78.dp) // Adjust the size as needed
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, //TODO sta cosa fa sempre il cuore vuoto
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
            DescriptionText(text = "Date: ${match.date}")
            DescriptionText(text = "Status: ${mapIntegerToMatchStatus(match.status)}")
        }
    }
}
