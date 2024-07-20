package com.example.tournaMake.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.AchievementsProfileViewModel
import com.example.tournaMake.data.models.AuthenticationViewModel
import com.example.tournaMake.data.models.ProfileViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.filemanager.AppDirectoryNames
import com.example.tournaMake.filemanager.PROFILE_PICTURE_NAME
import com.example.tournaMake.filemanager.ProfileImageHelper
import com.example.tournaMake.filemanager.ProfileImageHelperImpl
import com.example.tournaMake.filemanager.doesDirectoryContainFile
import com.example.tournaMake.filemanager.loadImageUriFromDirectory
import com.example.tournaMake.sampledata.AchievementPlayer
import com.example.tournaMake.sampledata.AchievementResult
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.ui.screens.profile.ProfileScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel

class ProfileActivity : ComponentActivity() {
    private var appDatabase: AppDatabase? = get<AppDatabase>()
    private val profilePictureHelper: ProfileImageHelper = ProfileImageHelperImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // See ThemeViewModel.kt
            val themeViewModel = koinViewModel<ThemeViewModel>()
            // The following line converts the StateFlow contained in the ViewModel
            // to a State object. State objects can trigger recompositions, while
            // StateFlow objects can't. The 'withLifecycle' part ensures this state
            // is destroyed when we leave this Activity.
            val state = themeViewModel.state.collectAsStateWithLifecycle()
            val authenticationViewModel = koinViewModel<AuthenticationViewModel>()
            val loggedEmail = authenticationViewModel.loggedEmail.collectAsStateWithLifecycle()
            val profileViewModel = koinViewModel<ProfileViewModel>()
            val profileObserver = Observer<MainProfile?> { profile ->
                Log.d("DEV", "In profile observer profile = ${profile?.email}")
            }
            profileViewModel.profileLiveData.observe(this, profileObserver)
            val achievementsProfileViewModelViewModel = koinViewModel<AchievementsProfileViewModel>()
            val achievementsObserver = Observer<List<AchievementResult>> { }
            achievementsProfileViewModelViewModel.achievementProfileListLiveData.observe(this, achievementsObserver)
            // Adding management of profile photo
            /* Code taken from:
            * https://www.youtube.com/watch?v=uHX5NB6wHao
            * */
            var selectedImageURI by remember {
                mutableStateOf<Uri?>(
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
                        profilePictureHelper.storeProfilePictureImmediately(
                            profileImageUri = uri,
                            email = loggedEmail.value.loggedProfileEmail,
                            contentResolver = contentResolver,
                            context = baseContext,
                            databaseUpdaterCallback = this::uploadPhotoToDatabase
                        )
                    selectedImageURI = uriForInternallySavedFile
                    recreate() // I'm sorry but without this line I don't see changes take effect
                } else if (uri != null && loggedEmail.value.loggedProfileEmail.isEmpty()) {
                    profilePictureHelper.waitForEmailThenStoreProfilePicture(
                        loggedEmailStateFlow = authenticationViewModel.loggedEmail,
                        profileImageUri = uri,
                        context = baseContext,
                        databaseUpdaterCallback = this::uploadPhotoToDatabase,
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
                    "DEV", "In onResult function in ProfileActivity.kt: everything went fine!"
                )
            }
            fetchAndUpdateProfile(
                loggedEmail.value.loggedProfileEmail, profileViewModel
            ) {
                selectedImageURI = it // callback for when the profile data come
            }
            fetchAndUpdateAchievementsProfile(loggedEmail.value.loggedProfileEmail, achievementsProfileViewModelViewModel)
            ProfileScreen(
                state = state.value,
                profileLiveData = profileViewModel.profileLiveData,
                achievementPlayerLiveData = achievementsProfileViewModelViewModel.achievementProfileListLiveData,
                backButton = this::finish,
                navigateToChart = this::navigateToChart,
                navigateToPlayerActivity = this::navigateToPlayerActivity,
                selectedImage = selectedImageURI,
                photoPickerLauncher = singlePhotoPickerLauncher
            )
        }
    }

    private fun uploadPhotoToDatabase(uri: Uri, loggedEmail: String) {
        Log.d("DEV", "Got logged email: $loggedEmail")
        // TODO: add database uri uploading
    }

    /**
     * Fetches data from database, mainly the profile email and location.
     * TODO: add number of played tournaments
     * */
    private fun fetchAndUpdateProfile(
        email: String, profileViewModel: ProfileViewModel, thenUpdateImageUri: (Uri?) -> Unit
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val myProfile = appDatabase?.mainProfileDao()?.getProfileByEmail(email)
                Log.d("DEV", "In getProfile() coroutine, myProfile.email = ${myProfile?.email}")
                // Now update the data in the view model, to trigger the onchange method of the attached
                // observer
                profileViewModel.changeProfileFromCoroutine(myProfile)
                val profileImageUri = loadImageUriFromDirectory(
                    dirName = AppDirectoryNames.profileImageDirectoryName,
                    imageName = PROFILE_PICTURE_NAME,
                    context = baseContext,
                    email = myProfile?.email
                )
                thenUpdateImageUri(profileImageUri)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun fetchAndUpdateAchievementsProfile(email: String, achievementsProfileViewModel: AchievementsProfileViewModel){
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val myAchievement = appDatabase?.achievementPlayerDao()?.getAchievementsByEmail(email)
                if (myAchievement != null) {
                    achievementsProfileViewModel.updateAchievementProfileList(myAchievement)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun navigateToChart() {
        val intent = Intent(this, GamesChartActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToPlayerActivity() {
        val intent = Intent(this, PlayerActActivity::class.java)
        startActivity(intent)
    }
}