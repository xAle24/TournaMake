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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
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
import com.example.tournaMake.sampledata.AchievementResult
import com.example.tournaMake.ui.screens.common.BasicScreenWithAppBars
import com.example.tournaMake.ui.theme.getThemeColors
import com.example.tournaMake.utils.rememberCameraLauncher
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
    val profilePictureHelper: ProfileImageHelper = ProfileImageHelperImpl()
    val themeViewModel = koinViewModel<ThemeViewModel>()
    val state by themeViewModel.state.collectAsStateWithLifecycle()
    val authenticationViewModel = koinViewModel<AuthenticationViewModel>()
    val loggedEmail = authenticationViewModel.loggedEmail.collectAsStateWithLifecycle()
    val profileViewModel = koinViewModel<ProfileViewModel>()
    val profileLiveData = profileViewModel.profileLiveData
    val achievementsProfileViewModel = koinViewModel<AchievementsProfileViewModel>()
    val achievementPlayerLiveData = achievementsProfileViewModel.achievementProfileListLiveData
    val achievementsObserver = Observer<List<AchievementResult>> { }
    achievementsProfileViewModel.achievementProfileListLiveData.observe(
        owner,
        achievementsObserver
    )
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
        if (uri != null && loggedEmail.value.loggedProfileEmail.isNotEmpty()) {
            val uriForInternallySavedFile =
                profilePictureHelper.storeProfilePictureImmediately(
                    profileImageUri = uri,
                    email = loggedEmail.value.loggedProfileEmail,
                    contentResolver = contentResolver,
                    context = context,
                    databaseUpdaterCallback = ::uploadPhotoToDatabase
                )
            selectedImageURI = uriForInternallySavedFile
        } else if (uri != null && loggedEmail.value.loggedProfileEmail.isEmpty()) {
            profilePictureHelper.waitForEmailThenStoreProfilePicture(
                loggedEmailStateFlow = authenticationViewModel.loggedEmail,
                profileImageUri = uri,
                context = context,
                databaseUpdaterCallback = ::uploadPhotoToDatabase,
                lifecycleCoroutineScope = owner.lifecycleScope,
                lifecycleOwner = owner,
                stateChangerCallback = { resultUri ->
                    selectedImageURI = resultUri
                },
                contentResolver = contentResolver
            )
        }
    }

    // To update profile picture by means of camera
    val cameraLauncher = rememberCameraLauncher { uri ->
        if (loggedEmail.value.loggedProfileEmail.isNotEmpty()) {
            val uriForInternallySavedFile =
                profilePictureHelper.storeProfilePictureImmediately(
                    profileImageUri = uri,
                    email = loggedEmail.value.loggedProfileEmail,
                    contentResolver = contentResolver,
                    context = context,
                    databaseUpdaterCallback = ::uploadPhotoToDatabase
                )
            selectedImageURI = uriForInternallySavedFile
        } else if (loggedEmail.value.loggedProfileEmail.isEmpty()) {
            profilePictureHelper.waitForEmailThenStoreProfilePicture(
                loggedEmailStateFlow = authenticationViewModel.loggedEmail,
                profileImageUri = uri,
                context = context,
                databaseUpdaterCallback = ::uploadPhotoToDatabase,
                lifecycleCoroutineScope = owner.lifecycleScope,
                lifecycleOwner = owner,
                stateChangerCallback = { resultUri ->
                    selectedImageURI = resultUri
                },
                contentResolver = contentResolver
            )
        }
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
    val profile = profileLiveData.observeAsState()
    val achievements = achievementPlayerLiveData.observeAsState()

    BasicScreenWithAppBars(
        state = state,
        backFunction = { navController.navigate(NavigationRoute.ProfilesListScreen.route) },
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
                    color = getThemeColors(themeState = state).getNormalTextColor()
                )
                Text("Achievements",
                    Modifier
                        .clickable { selectedTabIndex = 1 }
                        .padding(5.dp),
                    color = getThemeColors(themeState = state).getNormalTextColor()
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
                                Text(profile.value?.username ?: "Loading...", fontSize = 50.sp)
                                Text(
                                    "Latitude: ${profile.value?.locationLatitude ?: "Unknown"}\nLongitude: ${profile.value?.locationLongitude ?: "Unknown"}",
                                    style = androidx.compose.ui.text.TextStyle(fontSize = 20.sp)
                                )
                            }
                        }
                        Grid(
                            wonTournamentsNumber = profile.value?.wonTournamentsNumber ?: 0,
                            playedTournamentsNumber = /*profile.value.playedTournamentsNumber ?: 0*/ 0, // TODO: update
                            onChartClick = { navController.navigate(NavigationRoute.ChartScreen.route) },
                            onActivityClick = { navController.navigate(NavigationRoute.PlayerMatchesHistoryScreen.route) },
                            state = state
                        ) // TODO: add played tournaments number to database
                    }
                }

                1 -> {
                    achievements.value?.let { achievementList ->
                        Column {
                            achievementList.forEach { achievement ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = achievement.name,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Status" + achievement.status.toString(),
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Description" + achievement.description,
                                            color = MaterialTheme.colorScheme.secondary
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

