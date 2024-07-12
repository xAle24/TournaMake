package com.example.tournaMake.ui.screens.match

import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.example.tournaMake.R
import com.example.tournaMake.data.constants.MatchStatus
import com.example.tournaMake.data.constants.mapIntegerStatusToString
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.sampledata.Match
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme

@Composable
fun MatchScreenList(
    state: ThemeState,
    matchesListLiveData: LiveData<List<Match>>
) {
    // Data being fetched from database
    val matchesList = matchesListLiveData.observeAsState()

    BasicScreenWithTheme(
        state = state
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
        ) {
            if (matchesList.value != null) {
                items(matchesList.value!!) { item ->
                    // TODO: maybe this won't be necessary when it will be integrated with the database
                    var deleted by remember { mutableStateOf(false) }
                    if (!deleted) {
                        MatchCard(match = item, onDelete = { deleted = true })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchCard(
    match: Match,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        onClick = { expanded = !expanded },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
            Row {
                Image(
                    painter = painterResource(id = R.drawable.no_game_picture),
                    contentDescription = "Appropriate game image"
                )
                Column {
                    Text(text = "Date: ${match.date}")
                    Text(text = "Status: ${mapIntegerStatusToString(match.status)}")
                }
            }
            if (expanded) {
                Row {
                    IconButton( // the delete button
                        onClick = { onDelete() },
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete" )
                    }
                    IconButton(
                        onClick = { /* TODO: add navigation to match screen */ },
                        modifier = Modifier
                            .width(100.dp)
                            .height(50.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Continue")
                        Text("Continue")
                    }
                }
            }
        }
    }
}