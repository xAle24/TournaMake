package com.example.tournaMake.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.Companion
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.LoggedProfileViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.filemanager.AppDirectoryNames
import com.example.tournaMake.filemanager.PROFILE_PICTURE_NAME
import com.example.tournaMake.filemanager.ProfileImageHelper
import com.example.tournaMake.filemanager.ProfileImageHelperImpl
import com.example.tournaMake.filemanager.createDirectory
import com.example.tournaMake.filemanager.doesDirectoryContainFile
import com.example.tournaMake.filemanager.doesDirectoryExist
import com.example.tournaMake.filemanager.loadImageUriFromDirectory
import com.example.tournaMake.filemanager.saveImageToDirectory
import com.example.tournaMake.ui.screens.registration.RegistrationPhotoScreen
import org.koin.androidx.compose.koinViewModel
import java.nio.file.NoSuchFileException

class RegistrationPhotoActivity : ComponentActivity() {
    private val profileImageHelper: ProfileImageHelper = ProfileImageHelperImpl()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val state = themeViewModel.state.collectAsStateWithLifecycle()
            val loggedProfileViewModel = koinViewModel<LoggedProfileViewModel>()
            val loggedEmail = loggedProfileViewModel.loggedEmail.collectAsStateWithLifecycle()
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
                        profileImageHelper.storeProfilePictureImmediately(
                            profileImageUri = uri,
                            email = loggedEmail.value.loggedProfileEmail,
                            contentResolver = contentResolver,
                            context = baseContext,
                            databaseUpdaterCallback = this::uploadPhotoToDatabase
                        )
                    selectedImageURI = uriForInternallySavedFile
                    recreate() // I'm sorry but without this line I don't see changes take effect
                } else if (uri != null && loggedEmail.value.loggedProfileEmail.isEmpty()) {
                    profileImageHelper.waitForEmailThenStoreProfilePicture(
                        loggedEmailStateFlow = loggedProfileViewModel.loggedEmail,
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
                    "DEV", "In onResult function in RegistrationPhotoActivity.kt: everything went fine!"
                )
            }

            RegistrationPhotoScreen(
                state = state.value,
                back = this::back,
                loadMenu = this::loadMenu,
                selectedImage = selectedImageURI,
                photoPickerLauncher = singlePhotoPickerLauncher
            )
        }
    }

    private fun uploadPhotoToDatabase(uri: Uri, loggedEmail: String) {
        Log.d("DEV", "Got logged email: $loggedEmail")
        // TODO: add database uri uploading
    }

    private fun back() {
        finish()
    }

    private fun loadMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }
}