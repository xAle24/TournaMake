package com.example.tournaMake.ui.screens.match

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.tournaMake.data.constants.mapIntegerStatusToString
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.sampledata.Match
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme
import com.example.tournaMake.ui.theme.ColorConstants
import com.example.tournaMake.ui.theme.getThemeColors

@Composable
fun MatchListScreen(
    state: ThemeState,
    matchesListLiveData: LiveData<List<Match>>
) {
    // Data being fetched from database
    val matchesList = matchesListLiveData.observeAsState(emptyList())

    BasicScreenWithTheme(
        state = state
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
        ) {
            items(matchesList.value) { item ->
                // TODO: maybe this won't be necessary when it will be integrated with the database
                var deleted by remember { mutableStateOf(false) }
                if (!deleted) {
                    MatchCard(match = item, onDelete = { deleted = true }, themeState = state)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchCard(
    match: Match,
    onDelete: () -> Unit,
    themeState: ThemeState
) {
    var expanded by remember { mutableStateOf(false) }
    val colorConstants = getThemeColors(themeState = themeState) // see ui/theme/ColorConstants.kt
    Card(
        onClick = { expanded = !expanded },
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
        Text(
            match.gameID + " Match", // TODO: change with Game Name
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(0.dp, 10.dp),
            style = MaterialTheme.typography.displaySmall
        )
        Column(
            //Modifier.background(MaterialTheme.colorScheme.secondary)
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Content(match = match)
            if (expanded) {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                ) {
                    DeleteButton(onDelete = onDelete)
                    Spacer(modifier = Modifier.width(10.dp))
                    if (mapIntegerStatusToString(match.status) == "Ongoing") {
                        ContinueIconButton(colorConstants = colorConstants)
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
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
        modifier = Modifier
            .padding(5.dp, 5.dp)
    )
}

@Composable
fun Content(
    match: Match
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
            DescriptionText(text = "Status: ${mapIntegerStatusToString(match.status)}")
        }
    }
}

@Composable
fun DeleteButton(
    onDelete: () -> Unit
) {
    IconButton( // the delete button
        onClick = { onDelete() },
        modifier = Modifier
            .width(80.dp)
            .height(80.dp)
            .background(Color(198, 15, 15))
            .border(BorderStroke(3.dp, Color.White))
            .padding(5.dp, 5.dp)
    ) {
        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
    }
}

@Composable
fun ContinueIconButton(
    colorConstants: ColorConstants,
) {
    IconButton(
        onClick = { /* TODO: add navigation to match screen */ },
        modifier = Modifier
            .width(200.dp)
            .height(80.dp)
            .border(BorderStroke(3.dp, MaterialTheme.colorScheme.tertiary))
            .padding(5.dp, 5.dp)
            .background(colorConstants.getButtonBackground()),
    ) {
        Column {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Continue",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(40.dp)
            )
            Text("Continue", fontSize = 24.sp)
        }
    }
}