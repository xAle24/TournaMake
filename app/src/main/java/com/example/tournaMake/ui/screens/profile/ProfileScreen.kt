package com.example.tournaMake.ui.screens.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.tournaMake.R
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme
import com.example.tournaMake.ui.theme.getThemeColors

/**
 * The screen seen when clicking on a specific profile.
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    state: ThemeState,
    //profile: MainProfile?,
    profileLiveData: LiveData<MainProfile?>,
    backButton: () -> Unit,
    navigateToChart: () -> Unit,
    navigateToPlayerActivity: () -> Unit,
    selectedImage: Uri?,
    photoPickerLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>
) {
    /*
    * This extension function was imported with:
    * implementation ("androidx.compose.runtime:runtime-livedata:1.6.8")
    * */
    val profile = profileLiveData.observeAsState()
    Log.d("DEV", "In ProfileScreen.kt, profile email = ${profile.value?.email}")

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
            // Tabs for "Profile Info" and "Player Games"
            var selectedTabIndex by remember { mutableIntStateOf(0) }
            TabRow(
                selectedTabIndex = selectedTabIndex, modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary))
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.05f)
            ) {
                Text("Profile Info",
                    Modifier
                        .clickable { selectedTabIndex = 0 }
                        .padding(start = 5.dp),
                    color = getThemeColors(themeState = state).getNormalTextColor()
                )
                Text("Achievements",
                    Modifier
                        .clickable { selectedTabIndex = 1 }
                        .padding(start = 5.dp),
                    color = getThemeColors(themeState = state).getNormalTextColor()
                )
            }
            // Display content based on selected tab
            when (selectedTabIndex) {
                0 -> {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .fillMaxWidth(0.9f)
                            .fillMaxHeight(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProfileImage(selectedImage = selectedImage, photoPickerLauncher = photoPickerLauncher)
                            Column(
                                modifier = Modifier
                                    .padding(start = 10.dp)
                            ) {
                                Text(profile.value?.username ?: "Loading...", fontSize = 50.sp)
                                Text(
                                    "Latitude: ${profile.value?.locationLatitude ?: "Unknown"}\nLongitude: ${profile.value?.locationLongitude ?: "Unknown"}",
                                    style = androidx.compose.ui.text.TextStyle(fontSize = 20.sp)
                                )
                            }
                        }
                        Grid(
                            wonTournamentsNumber = profile.value?.wonTournamentsNumber ?: 0,
                            playedTournamentsNumber = /*profile.value.playedTournamentsNumber ?: 0*/ 0,
                            onChartClick = navigateToChart,
                            onActivityClick = navigateToPlayerActivity,
                            state = state
                        ) // TODO: add played tournaments number to database
                    }
                }

                1 -> {
                    // Achievements
                }
            }
        }
    }
}

@Composable
fun ProfileImage(
    selectedImage: Uri?,
    photoPickerLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>
) {
    key(selectedImage) {
        if (selectedImage == null) {
            Image(
                painter = painterResource(id = R.drawable.no_profile_picture_icon), //TODO: aggiungere foto profilo
                contentDescription = "Avatar",
                modifier = Modifier
                    .padding(4.dp)
                    .width(150.dp)
                    .height(150.dp)
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            )
        } else {
            AsyncImage(
                model = createImageRequest(LocalContext.current, selectedImage),
                contentDescription = null,
                modifier = Modifier
                    .padding(4.dp)
                    .width(150.dp)
                    .height(150.dp)
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

@Composable
fun Grid(
    wonTournamentsNumber: Int = 0,
    playedTournamentsNumber: Int = 0,
    state: ThemeState,
    onChartClick: () -> Unit,
    onActivityClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ProfileCard(
                text = "Won Tournaments",
                score = "$wonTournamentsNumber",
                backgroundColor = MaterialTheme.colorScheme.secondary,
                borderColor = MaterialTheme.colorScheme.tertiary,
                state = state
            )
            ProfileCard(
                text = "Played Tournaments",
                score = "$playedTournamentsNumber",
                backgroundColor = MaterialTheme.colorScheme.secondary,
                borderColor = MaterialTheme.colorScheme.tertiary,
                state = state
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ProfileCardButton(
                text = "Charts",
                backgroundBrush = getThemeColors(themeState = state).getButtonBackground(),
                borderColor = MaterialTheme.colorScheme.outline,
                imagePainter = painterResource(id = R.drawable.chart),
                onClick = onChartClick
            )
            ProfileCardButton(
                text = "Activity",
                backgroundBrush = getThemeColors(themeState = state).getButtonBackground(),
                borderColor = MaterialTheme.colorScheme.outline,
                imagePainter = painterResource(id = R.drawable.calendar),
                onClick = onActivityClick
            )
        }
    }
}

@Composable
fun ProfileCard(
    text: String,
    score: String?,
    backgroundColor: Color?,
    borderColor: Color?,
    state: ThemeState
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(150.dp),
        border = BorderStroke(5.dp, borderColor ?: MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor ?: MaterialTheme.colorScheme.secondary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            if (score != null) {
                Text(
                    text = score,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = getThemeColors(themeState = state).getNormalTextColor()
                )
            }
            Text(
                text = text,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(),
                color = getThemeColors(themeState = state).getNormalTextColor()
            )
        }
    }
}

@Composable
fun ProfileCardButton(
    text: String,
    backgroundBrush: Brush,
    borderColor: Color?,
    imagePainter: Painter,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundBrush)
            .clickable { onClick() },
        border = BorderStroke(5.dp, borderColor ?: MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(
                painter = imagePainter,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = text,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun MyGridPreview() {
    Grid(
        state = ThemeState(ThemeEnum.Light),
        onChartClick = {},
        onActivityClick = {}
    )
}

/**
 * This is needed because oftentimes newUri is equal to the value saved in
 * the "selectedImage" var in the setContent function of this activity,
 * so the composables don't "feel the urge" to update the image.
 * */
fun createImageRequest(context: Context, uri: Uri): ImageRequest {
    return ImageRequest.Builder(context)
        .data(uri)
        .diskCachePolicy(CachePolicy.DISABLED)
        .memoryCachePolicy(CachePolicy.DISABLED)
        .build()
}

/*@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        state = ThemeState(ThemeEnum.Light),
        profileLiveData = MutableLiveData<MainProfile?>(
            MainProfile(
                email = "alin@gmail",
                username = "Alin",
                password = "",
                profileImage = "",
                wonTournamentsNumber = 0,
                locationLatitude = 0.0f,
                locationLongitude = 0.0f
            )
        ),
        backButton = { *//*TODO*//* },
        navigateToChart = { *//*TODO*//* },

    )
}*/

