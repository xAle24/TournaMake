package com.example.tournaMake.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.AuthenticationViewModel
import com.example.tournaMake.data.models.CoordinatesViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.filemanager.AppDirectoryNames
import com.example.tournaMake.filemanager.PROFILE_PICTURE_NAME
import com.example.tournaMake.filemanager.ProfileImageHelper
import com.example.tournaMake.filemanager.ProfileImageHelperImpl
import com.example.tournaMake.filemanager.doesDirectoryContainFile
import com.example.tournaMake.filemanager.loadImageUriFromDirectory
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.ui.screens.registration.RegistrationPhotoScreen
import com.example.tournaMake.utils.Coordinates
import com.example.tournaMake.utils.LocationService
import com.example.tournaMake.utils.PermissionStatus
import com.example.tournaMake.utils.StartMonitoringResult
import com.example.tournaMake.utils.rememberPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

class RegistrationPhotoActivity : ComponentActivity() {
    private val profileImageHelper: ProfileImageHelper = ProfileImageHelperImpl()
    private lateinit var locationService: LocationService
    private val appDatabase: AppDatabase by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationService = LocationService(this)

        setContent {
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val state = themeViewModel.state.collectAsStateWithLifecycle()
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
                            baseContext,
                            loggedEmail.value.loggedProfileEmail
                        )
                    ) loadImageUriFromDirectory(
                        AppDirectoryNames.profileImageDirectoryName,
                        PROFILE_PICTURE_NAME,
                        baseContext,
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
                            context = baseContext,
                            databaseUpdaterCallback = this::updateDatabaseWithPhotoUri
                        )
                    selectedImageURI = uriForInternallySavedFile
                    recreate() // I'm sorry but without this line I don't see changes take effect
                } else if (uri != null && loggedEmail.value.loggedProfileEmail.isEmpty()) {
                    profileImageHelper.waitForEmailThenStoreProfilePicture(
                        loggedEmailStateFlow = authenticationViewModel.loggedEmail,
                        profileImageUri = uri,
                        context = baseContext,
                        databaseUpdaterCallback = this::updateDatabaseWithPhotoUri,
                        lifecycleCoroutineScope = lifecycleScope,
                        lifecycleOwner = this,
                        stateChangerCallback = { resultUri ->
                            selectedImageURI = resultUri
                            recreate()
                        },
                        contentResolver = contentResolver
                    )
                }
                Log.d(
                    "DEV",
                    "In onResult function in RegistrationPhotoActivity.kt: everything went fine!"
                )
            }

            // GPS variables
            val snackbarHostState = remember { SnackbarHostState() }
            var showLocationDisabledAlert by remember { mutableStateOf(false) }
            var showPermissionDeniedAlert by remember { mutableStateOf(false) }
            var showPermissionPermanentlyDeniedSnackbar by remember { mutableStateOf(false) }
            val coordinatesViewModel = koinViewModel<CoordinatesViewModel>()

            // Setting the callback... hope it works
            locationService.addCallback {
                updateDatabaseWithCoordinates(
                    loggedEmail.value.loggedProfileEmail,
                    it.latitude,
                    it.longitude,
                    coordinatesViewModel
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

            RegistrationPhotoScreen(
                state = state.value,
                back = this::back,
                loadMenu = this::loadMenu,
                selectedImage = selectedImageURI,
                photoPickerLauncher = singlePhotoPickerLauncher,
                snackbarHostState = snackbarHostState,
                requestLocation = ::requestLocation,
                coordinatesLiveData = coordinatesViewModel.coordinatesLiveData
            )

            if (showLocationDisabledAlert) {
                AlertDialog(
                    title = { Text("Location disabled") },
                    text = { Text("Location must be enabled to get your current location in the app.") },
                    confirmButton = {
                        TextButton(onClick = {
                            locationService.openLocationSettings()
                            showLocationDisabledAlert = false
                        }) {
                            Text("Enable")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLocationDisabledAlert = false }) {
                            Text("Dismiss")
                        }
                    },
                    onDismissRequest = { showLocationDisabledAlert = false }
                )
            }

            if (showPermissionDeniedAlert) {
                AlertDialog(
                    title = { Text("Location permission denied") },
                    text = { Text("Location permission is required to get your current location in the app.") },
                    confirmButton = {
                        TextButton(onClick = {
                            locationPermission.launchPermissionRequest()
                            showPermissionDeniedAlert = false
                        }) {
                            Text("Grant")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPermissionDeniedAlert = false }) {
                            Text("Dismiss")
                        }
                    },
                    onDismissRequest = { showPermissionDeniedAlert = false }
                )
            }

            val ctx = LocalContext.current
            if (showPermissionPermanentlyDeniedSnackbar) {
                LaunchedEffect(snackbarHostState) {
                    val res = snackbarHostState.showSnackbar(
                        "Location permission is required.",
                        "Go to Settings",
                        duration = SnackbarDuration.Long
                    )
                    if (res == SnackbarResult.ActionPerformed) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", ctx.packageName, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        if (intent.resolveActivity(ctx.packageManager) != null) {
                            ctx.startActivity(intent)
                        }
                    }
                    showPermissionPermanentlyDeniedSnackbar = false
                }
            }
        }
    }

    private fun updateDatabaseWithPhotoUri(
        uri: Uri,
        loggedEmail: String
    ) {
        Log.d("DEV", "Got logged email: $loggedEmail")
        lifecycleScope.launch(Dispatchers.IO) {
            val mainProfile = appDatabase.mainProfileDao().getProfileByEmail(loggedEmail)
            val updatedMainProfile = MainProfile(
                username = mainProfile.username,
                password = mainProfile.password,
                email = mainProfile.email,
                profileImage = uri.toString(),
                wonTournamentsNumber = mainProfile.wonTournamentsNumber,
                locationLatitude = mainProfile.locationLatitude,
                locationLongitude = mainProfile.locationLongitude
            )
            appDatabase.mainProfileDao().upsert(updatedMainProfile)
        }
    }

    private fun updateDatabaseWithCoordinates(
        loggedEmail: String,
        latitude: Double,
        longitude: Double,
        coordinatesViewModel: CoordinatesViewModel
    ) {
        Log.d(
            "DEV",
            "In RegistrationPhotoActivity, coordinates are: latitude = $latitude, longitude = $longitude"
        )
        try {
            lifecycleScope.launch(Dispatchers.IO) {
                val mainProfile = appDatabase.mainProfileDao().getProfileByEmail(loggedEmail)
                val updatedMainProfile = MainProfile(
                    username = mainProfile.username,
                    password = mainProfile.password,
                    email = mainProfile.email,
                    profileImage = mainProfile.profileImage,
                    wonTournamentsNumber = mainProfile.wonTournamentsNumber,
                    locationLatitude = latitude,
                    locationLongitude = longitude
                )
                appDatabase.mainProfileDao().upsert(updatedMainProfile)
                coordinatesViewModel.changeCoordinates(Coordinates(latitude, longitude))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun back() {
        finish()
    }

    private fun loadMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }

    override fun onPause() {
        super.onPause()
        locationService.pauseLocationRequest()
    }

    override fun onResume() {
        super.onResume()
        locationService.resumeLocationRequest()
    }
}