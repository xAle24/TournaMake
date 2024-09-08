package com.example.tournaMake.ui.screens.registration

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.tournaMake.R
import com.example.tournaMake.activities.navgraph.NavigationRoute
import com.example.tournaMake.activities.updateDatabaseWithCoordinates
import com.example.tournaMake.activities.updateDatabaseWithPhotoUri
import com.example.tournaMake.data.models.AuthenticationViewModel
import com.example.tournaMake.data.models.CoordinatesViewModel
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.filemanager.AppDirectoryNames
import com.example.tournaMake.filemanager.PROFILE_PICTURE_NAME
import com.example.tournaMake.filemanager.ProfileImageHelper
import com.example.tournaMake.filemanager.ProfileImageHelperImpl
import com.example.tournaMake.filemanager.doesDirectoryContainFile
import com.example.tournaMake.filemanager.loadImageUriFromDirectory
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme
import com.example.tournaMake.ui.theme.ColorConstants
import com.example.tournaMake.ui.theme.getThemeColors
import com.example.tournaMake.utils.Coordinates
import com.example.tournaMake.utils.LocationService
import com.example.tournaMake.utils.PermissionStatus
import com.example.tournaMake.utils.StartMonitoringResult
import com.example.tournaMake.utils.rememberCameraLauncher
import com.example.tournaMake.utils.rememberPermission
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegistrationPhotoScreen(
    navController: NavController,
    owner: LifecycleOwner,
    contentResolver: ContentResolver
) {
    // Initial variables
    val context = LocalContext.current
    val profileImageHelper: ProfileImageHelper = ProfileImageHelperImpl()
    val locationService = LocationService(context)

    // Logic that was previously in the activity
    val themeViewModel = koinViewModel<ThemeViewModel>()
    val state by themeViewModel.state.collectAsStateWithLifecycle()
    val colorConstants = getThemeColors(themeState = state)
    val authenticationViewModel = koinViewModel<AuthenticationViewModel>()
    val loggedEmail = authenticationViewModel.loggedEmail.collectAsStateWithLifecycle()
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
                profileImageHelper.storeProfilePictureImmediately(
                    profileImageUri = uri,
                    email = loggedEmail.value.loggedProfileEmail,
                    contentResolver = contentResolver,
                    context = context,
                    databaseUpdaterCallback = {outputUri, outputString -> updateDatabaseWithPhotoUri(
                        uri = outputUri,
                        loggedEmail = outputString,
                        owner = owner
                    )}
                )
            selectedImageURI = uriForInternallySavedFile
        } else if (uri != null && loggedEmail.value.loggedProfileEmail.isEmpty()) {
            profileImageHelper.waitForEmailThenStoreProfilePicture(
                loggedEmailStateFlow = authenticationViewModel.loggedEmail,
                profileImageUri = uri,
                context = context,
                databaseUpdaterCallback = {outputUri, outputString -> updateDatabaseWithPhotoUri(
                    uri = outputUri,
                    loggedEmail = outputString,
                    owner = owner
                )},
                lifecycleCoroutineScope = owner.lifecycleScope,
                lifecycleOwner = owner,
                stateChangerCallback = { resultUri ->
                    selectedImageURI = resultUri
                },
                contentResolver = contentResolver
            )
        }
        Log.d(
            "DEV",
            "In onResult function in RegistrationPhoto.kt: everything went fine!"
        )
    }

    // Camera launcher; code taken from tutor Gianni
    // The code inside here is a duplicate of the one above... maybe there's a way to refactor this
    val cameraLauncher = rememberCameraLauncher { uri ->
        if (loggedEmail.value.loggedProfileEmail.isNotEmpty()) {
            val uriForInternallySavedFile =
                profileImageHelper.storeProfilePictureImmediately(
                    profileImageUri = uri,
                    email = loggedEmail.value.loggedProfileEmail,
                    contentResolver = contentResolver,
                    context = context,
                    databaseUpdaterCallback = {outputUri, outputString -> updateDatabaseWithPhotoUri(
                        uri = outputUri,
                        loggedEmail = outputString,
                        owner = owner
                    )}
                )
            selectedImageURI = uriForInternallySavedFile
        } else if (loggedEmail.value.loggedProfileEmail.isEmpty()) {
            profileImageHelper.waitForEmailThenStoreProfilePicture(
                loggedEmailStateFlow = authenticationViewModel.loggedEmail,
                profileImageUri = uri,
                context = context,
                databaseUpdaterCallback = {outputUri, outputString -> updateDatabaseWithPhotoUri(
                    uri = outputUri,
                    loggedEmail = outputString,
                    owner = owner
                )},
                lifecycleCoroutineScope = owner.lifecycleScope,
                lifecycleOwner = owner,
                stateChangerCallback = { resultUri ->
                    selectedImageURI = resultUri
                },
                contentResolver = contentResolver
            )
        }
    }

    // GPS variables
    val snackbarHostState = remember { SnackbarHostState() }
    var showLocationDisabledAlert by remember { mutableStateOf(false) }
    var showPermissionDeniedAlert by remember { mutableStateOf(false) }
    var showPermissionPermanentlyDeniedSnackbar by remember { mutableStateOf(false) }
    val coordinatesViewModel = koinViewModel<CoordinatesViewModel>()
    val coordinatesLiveData = coordinatesViewModel.coordinatesLiveData
    // Setting the callback... hope it works
    locationService.addCallback {
        updateDatabaseWithCoordinates(
            loggedEmail.value.loggedProfileEmail,
            it.latitude,
            it.longitude,
            coordinatesViewModel,
            owner
        )
    }


    val locationPermission = rememberPermission(
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) { status ->
        when (status) {
            PermissionStatus.Granted -> {
                val res = locationService.requestCurrentLocation()
                showLocationDisabledAlert = res == StartMonitoringResult.GPSDisabled
            }

            PermissionStatus.Denied ->
                showPermissionDeniedAlert = true

            PermissionStatus.PermanentlyDenied ->
                showPermissionPermanentlyDeniedSnackbar = true

            PermissionStatus.Unknown -> {}
        }
    }

    fun requestLocation() {
        if (locationPermission.status.isGranted) {
            val res = locationService.requestCurrentLocation()
            showLocationDisabledAlert = res == StartMonitoringResult.GPSDisabled
        } else {
            locationPermission.launchPermissionRequest()
        }
    }

    // val screenHeight = configuration.screenHeightDp
    val coordinates = coordinatesLiveData.observeAsState()

    BasicScreenWithTheme(state = state) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            key(selectedImageURI) {
                if (selectedImageURI != null) {
                    /* The profile image will be contained here. */
                    AsyncImage(
                        model = createImageRequest(LocalContext.current, selectedImageURI!!),
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                BorderStroke(4.dp, MaterialTheme.colorScheme.primary)
                            ),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.no_profile_picture_icon),
                        contentDescription = "Appropriate logo image",
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                BorderStroke(4.dp, MaterialTheme.colorScheme.primary)
                            ),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Button(
                onClick = {
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .height(60.dp)
                    .fillMaxWidth(0.9f)
                    .background(colorConstants.getButtonBackground()),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                border = BorderStroke(3.dp, MaterialTheme.colorScheme.outline)
            ) {
                Text(
                    text = "Upload Photo",
                    fontSize = 30.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Adding button to take picture with Camera
            Button(
                onClick = {
                    cameraLauncher.captureImage()
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .height(60.dp)
                    .fillMaxWidth(0.9f)
                    .background(colorConstants.getButtonBackground()),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                border = BorderStroke(3.dp, MaterialTheme.colorScheme.outline)
            ) {
                Text(
                    text = "Take picture",
                    fontSize = 30.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            LocationUIArea(
                snackbarHostState = snackbarHostState,
                requestLocation = ::requestLocation,
                state = state,
                colorConstants = colorConstants,
                coordinates = coordinates.value
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Absolute.SpaceAround
            ) {
                Button(
                    onClick = { navController.navigate(NavigationRoute.RegistrationScreen.route) },
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .height(60.dp)
                        .fillMaxWidth(0.4f)
                        .background(colorConstants.getButtonBackground()),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(3.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(text = "Back")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = { navController.navigate(NavigationRoute.MenuScreen.route) },
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .height(60.dp)
                        .fillMaxWidth(0.7f)
                        .background(colorConstants.getButtonBackground()),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(3.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(text = "Next")
                }
            }
        }
    }
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

@Composable
fun LocationUIArea(
    snackbarHostState: SnackbarHostState,
    requestLocation: () -> Unit,
    state: ThemeState,
    colorConstants: ColorConstants,
    coordinates: Coordinates?
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(150.dp)
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            Button(onClick = { requestLocation() },
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                .height(60.dp)
                .fillMaxWidth(0.9f)
                .background(colorConstants.getButtonBackground()),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                border = BorderStroke(3.dp, MaterialTheme.colorScheme.outline)
            ) {
                Text("Get current location")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Latitude: ${coordinates?.latitude ?: "-"}", color = getThemeColors(
                    themeState = state
                ).getNormalTextColor()
            )
            Text(
                "Longitude: ${coordinates?.longitude ?: "-"}",
                color = getThemeColors(
                    themeState = state
                ).getNormalTextColor()
            )
        }
    }
}


