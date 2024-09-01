package com.example.tournaMake.ui.screens.match

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.tournaMake.R
import com.example.tournaMake.data.models.MatchDetailsViewModel
import com.example.tournaMake.data.models.TeamDataPacket
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.data.repositories.MatchDetailsRepository
import com.example.tournaMake.dataStore
import com.example.tournaMake.sampledata.MatchTM
import com.example.tournaMake.sampledata.Team
import com.example.tournaMake.ui.screens.common.BasicScreenWithAppBars
import com.example.tournaMake.ui.screens.common.RectangleContainer
import com.example.tournaMake.ui.screens.registration.createImageRequest

private val spacerHeight = 20.dp

@Composable
fun MatchDetailsScreen(
    state: ThemeState,
    gameImage: Uri?,
    backFunction: () -> Unit,
    vm: MatchDetailsViewModel,
    addMatchToFavorites: (String) -> Unit,
    removeMatchFromFavorites: (String) -> Unit,
) {
    val playedGameLiveData = vm.playedGame.observeAsState()
    val dataPackets by vm.teamDataPackets.observeAsState()
    val match by vm.match.observeAsState()
    BasicScreenWithAppBars(
        state = state,
        backFunction = backFunction,
        showTopBar = true,
        showBottomBar = false
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            MatchDetailsHeading(
                gameImage = gameImage,
                gameName = playedGameLiveData.value?.name ?: "Loading...",
                match,
                addMatchToFavorites,
                removeMatchFromFavorites
            )
            Spacer(Modifier.height(spacerHeight))
            dataPackets?.forEach { team ->
                TeamElementInMatchDetailsScreen(dataPacket = team)
                Spacer(modifier = Modifier.height(spacerHeight))
            }
        }
    }
}

@Composable
fun MatchDetailsHeading(
    gameImage: Uri?,
    gameName: String,
    match: MatchTM?,
    addMatchToFavorites: (String) -> Unit,
    removeMatchFromFavorites: (String) -> Unit
) {
    var isFavorite by remember { mutableStateOf(match?.favorites == 1) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max) // set as high as the highest child
    ) {
        RectangleContainer(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                .fillMaxWidth(0.9f)
                .align(Alignment.Center)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                //.background(Color.Black)
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                if (gameImage != null) {
                    AsyncImage(
                        model = gameImage,
                        contentDescription = "Game image"
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.no_game_picture),
                        contentDescription = "No game image found",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(start = 10.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Match", style = MaterialTheme.typography.headlineSmall)
                    Text(gameName)
                }
                IconButton(
                    onClick = {
                        isFavorite = if (!isFavorite) {
                            if (match != null) {
                                addMatchToFavorites(match.matchTmID)
                            }
                            true
                        } else {
                            if (match != null) {
                                removeMatchFromFavorites(match.matchTmID)
                            }
                            false
                        }
                    },
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favourite Match indicator",
                    )
                }
            }
        }
    }
}

@Composable
fun TeamElementInMatchDetailsScreen(
    dataPacket: TeamDataPacket
) {
    val teamScore = dataPacket.teamScore
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max) // set as high as the highest child
    ) {
        RectangleContainer(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f))
                .fillMaxWidth(0.9f)
                .align(Alignment.Center)
        ) {
            Text(
                dataPacket.teamUI.getTeamName(),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(start = 10.dp)
            )
            HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.onSecondary)
            Spacer(modifier = Modifier.height(spacerHeight))
            LazyRow(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                items(dataPacket.teamUI.getGuestProfiles().toList()) {
                    MiniProfileImageMatchDetails(profileName = it.username)
                }
                items(dataPacket.teamUI.getMainProfiles().toList()) {
                    MiniProfileImageMatchDetails(imageUri = it.profileImage?.toUri(), profileName = it.username)
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Score",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth(),
                    //.background(Color.White),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = teamScore.toString(),
                    fontSize = 30.sp
                )
            }
            Spacer(modifier = Modifier.height(spacerHeight))
        }
    }
}

@Composable
fun MiniProfileImageMatchDetails(
    imageUri: Uri? = null,
    profileName: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedCard(
            modifier = Modifier
                //.background(Color.White)
                .width(80.dp)
                .height(80.dp)
                .padding(4.dp),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(width = 2.dp, MaterialTheme.colorScheme.outline)
        ) {
            // Load your image here using imageResId
            // Example: Image(painter = painterResource(id = imageResId), contentDescription = null)
            // Text(username, modifier = Modifier.padding(4.dp))
            if (imageUri == null) {
                Image(
                    painterResource(id = R.drawable.no_profile_picture_icon),
                    contentDescription = null
                )
            } else {
                AsyncImage(
                    model = createImageRequest(LocalContext.current, imageUri),
                    contentDescription = null
                )
            }
        }
        Text(profileName)
    }
    Spacer(modifier = Modifier.width(8.dp))
}

@Preview
@Composable
fun MatchDetailsScreenPreview() {
    val vm = MatchDetailsViewModel(MatchDetailsRepository(LocalContext.current.dataStore))
    vm.changeTeamDataPackets(listOf(
        TeamDataPacket(testTeam1, 200, "team1ID", true),
        TeamDataPacket(testTeam2, 100, "team2ID", false)
    ))
    MatchDetailsScreen(
        state = ThemeState(ThemeEnum.Light),
        null,
        backFunction = {},
        vm = vm,
        addMatchToFavorites = {},
        removeMatchFromFavorites = {}
    )
}