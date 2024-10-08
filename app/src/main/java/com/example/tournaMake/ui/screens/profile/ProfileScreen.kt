package com.example.tournaMake.ui.screens.profile

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.tournaMake.R
import com.example.tournaMake.activities.fetchAndUpdateAchievementsProfile
import com.example.tournaMake.activities.fetchAndUpdateProfile
import com.example.tournaMake.activities.navgraph.NavigationRoute
import com.example.tournaMake.activities.uploadPhotoToDatabase
import com.example.tournaMake.data.models.AchievementsProfileViewModel
import com.example.tournaMake.data.models.AuthenticationViewModel
import com.example.tournaMake.data.models.ProfileViewModel
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.filemanager.AppDirectoryNames
import com.example.tournaMake.filemanager.PROFILE_PICTURE_NAME
import com.example.tournaMake.filemanager.ProfileImageHelper
import com.example.tournaMake.filemanager.ProfileImageHelperImpl
import com.example.tournaMake.filemanager.doesDirectoryContainFile
import com.example.tournaMake.filemanager.loadImageUriFromDirectory
import com.example.tournaMake.filemanager.saveImageToDirectory
import com.example.tournaMake.filemanager.storePhotoInInternalStorage
import com.example.tournaMake.ui.screens.common.BasicScreenWithAppBars
import com.example.tournaMake.ui.theme.getThemeColors
import com.example.tournaMake.utils.rememberCameraLauncher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 * The screen seen when clicking on a specific profile.
 * */
@Composable
fun ProfileScreen(
    navController: NavController,
    contentResolver: ContentResolver,
    owner: LifecycleOwner
) {
    val context = LocalContext.current
    val themeViewModel = koinViewModel<ThemeViewModel>()
    val state by themeViewModel.state.collectAsStateWithLifecycle()
    val authenticationViewModel = koinViewModel<AuthenticationViewModel>()
    val loggedEmail = authenticationViewModel.loggedEmail.collectAsStateWithLifecycle()
    val profileViewModel = koinViewModel<ProfileViewModel>()
    val achievementsProfileViewModel = koinViewModel<AchievementsProfileViewModel>()
    // Adding management of profile photo
    /* Code taken from:
    * https://www.youtube.com/watch?v=uHX5NB6wHao
    * */
    var selectedImageURI by remember {
        mutableStateOf(
            if (doesDirectoryContainFile(
                    AppDirectoryNames.profileImageDirectoryName,
                    PROFILE_PICTURE_NAME,
                    context,
                    loggedEmail.value.loggedProfileEmail
                )
            ) loadImageUriFromDirectory(
                AppDirectoryNames.profileImageDirectoryName,
                PROFILE_PICTURE_NAME,
                context,
                loggedEmail.value.loggedProfileEmail
            ) else null
        )
    }
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            owner.lifecycleScope.launch(Dispatchers.IO) {
                /**
                 * This function is called in order to save the profile image
                 * in a custom directory. This is needed to avoid overwriting
                 * the other profile pictures, since they all use a standard
                 * name (because I don't like it that the phone can choose
                 * what name to give the pictures automatically).
                 * */
                storePhotoInInternalStorage(
                    uri = uri,
                    email = loggedEmail.value.loggedProfileEmail,
                    context = context,
                    contentResolver = contentResolver
                )
                // Updating the database
                profileViewModel.uploadPhotoToDatabase(loggedEmail.value.loggedProfileEmail, uri)
            }
        }
        selectedImageURI = uri
    }

    // To update profile picture by means of camera
    val cameraLauncher = rememberCameraLauncher { uri ->
        uri.let {
            owner.lifecycleScope.launch(Dispatchers.IO) {
                /**
                 * This function is called in order to save the profile image
                 * in a custom directory. This is needed to avoid overwriting
                 * the other profile pictures, since they all use a standard
                 * name (because I don't like it that the phone can choose
                 * what name to give the pictures automatically).
                 * */
                storePhotoInInternalStorage(
                    uri = uri,
                    email = loggedEmail.value.loggedProfileEmail,
                    context = context,
                    contentResolver = contentResolver
                )
                // Updating the database
                profileViewModel.uploadPhotoToDatabase(loggedEmail.value.loggedProfileEmail, uri)
            }
        }
        selectedImageURI = uri
    }

    fetchAndUpdateProfile(
        loggedEmail.value.loggedProfileEmail,
        profileViewModel,
        thenUpdateImageUri = { selectedImageURI = it },
        baseContext = context,
        owner = owner
    )
    fetchAndUpdateAchievementsProfile(
        loggedEmail.value.loggedProfileEmail,
        achievementsProfileViewModel,
        owner = owner
    )
    /*
    * This extension function was imported with:
    * implementation ("androidx.compose.runtime:runtime-livedata:1.6.8")
    * */
    val profile = profileViewModel.profileLiveData.observeAsState()
    val playedTournamentsNumber = profileViewModel.playedTournaments.observeAsState()
    val achievements = achievementsProfileViewModel.achievementProfileListLiveData.observeAsState()

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
                        .padding(5.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text("Achievements",
                    Modifier
                        .clickable { selectedTabIndex = 1 }
                        .padding(5.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
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
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(start = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                ProfileImage(
                                    selectedImage = selectedImageURI,
                                    photoPickerLauncher = singlePhotoPickerLauncher
                                )
                                Button(
                                    onClick = {
                                        cameraLauncher.captureImage()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Text("Take picture")
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .padding(start = 10.dp)
                            ) {
                                Text(
                                    profile.value?.username ?: "Loading...",
                                    fontSize = 50.sp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(
                                    "Latitude: ${profile.value?.locationLatitude ?: "Unknown"}\nLongitude: ${profile.value?.locationLongitude ?: "Unknown"}",
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        Grid(
                            wonTournamentsNumber = profile.value?.wonTournamentsNumber ?: 0,
                            playedTournamentsNumber = playedTournamentsNumber.value ?: 0,
                            onChartClick = { navController.navigate(NavigationRoute.ChartScreen.route) },
                            onActivityClick = { navController.navigate(NavigationRoute.PlayerMatchesHistoryScreen.route) },
                            state = state
                        )
                    }
                }

                1 -> {
                    achievements.value?.let { achievementList ->
                        Column {
                            achievementList.forEach { achievement ->
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = CardDefaults.cardElevation(),
                                    modifier = Modifier
                                        .fillMaxWidth(0.95f)
                                        .padding(4.dp),
                                    colors = CardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                        disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                                        disabledContentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = achievement.name,
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Status: " + if (achievement.status == 1) "Completed" else "Not completed",
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Description:" + achievement.description,
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
}

@Composable
fun ProfileImage(
    selectedImage: Uri?,
    photoPickerLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>
) {
    key(selectedImage) {
        if (selectedImage == null) {
            Image(
                painter = painterResource(id = R.drawable.no_profile_picture_icon),
                contentDescription = "Avatar",
                modifier = Modifier
                    .padding(4.dp)
                    .width(150.dp)
                    .height(150.dp)
                    .border(BorderStroke(3.dp, color = MaterialTheme.colorScheme.secondary))
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
                    .border(BorderStroke(3.dp, color = MaterialTheme.colorScheme.secondary))
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
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.onPrimary
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

