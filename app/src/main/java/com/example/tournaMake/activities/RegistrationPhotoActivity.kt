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
import com.example.tournaMake.data.models.LoggedProfileViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.filemanager.AppDirectoryNames
import com.example.tournaMake.filemanager.PROFILE_PICTURE_NAME
import com.example.tournaMake.filemanager.createDirectory
import com.example.tournaMake.filemanager.doesDirectoryExist
import com.example.tournaMake.filemanager.loadImageUriFromDirectory
import com.example.tournaMake.filemanager.saveImageToDirectory
import com.example.tournaMake.ui.screens.registration.RegistrationPhotoScreen
import org.koin.androidx.compose.koinViewModel
import java.nio.file.NoSuchFileException

class RegistrationPhotoActivity : ComponentActivity() {
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
                mutableStateOf<Uri?>(null)
            }
            val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia(),
                onResult = { uri ->
                    if (uri != null) {
                        selectedImageURI = uri
                        uploadPhotoToDatabase(uri, loggedEmail.value.loggedProfileEmail)
                    }
                }
            )

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
    private fun storePhotoInInternalStorage(uri: Uri, email: String?): Uri {
        val context = baseContext
        if (!doesDirectoryExist(AppDirectoryNames.profileImageDirectoryName, context, email)) {
            createDirectory(AppDirectoryNames.profileImageDirectoryName, context, email)
            Log.d("DEV", "In RegistratinPhotoActivity.kt, " +
                    "storePhotoInInternalStorage - Created directory!")
        }
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        saveImageToDirectory(
            bitmap = bitmap,
            context = context,
            dirName = AppDirectoryNames.profileImageDirectoryName, // defined in filemanager/FileUtils.kt
            imageName = PROFILE_PICTURE_NAME, // defined in filemanager/FileUtils.kt
            email = email
        )
        return loadImageUriFromDirectory(
            dirName = AppDirectoryNames.profileImageDirectoryName,
            imageName = PROFILE_PICTURE_NAME,
            context = context,
            email = email
        ) ?: throw NoSuchFileException("No profile image uri was found for email $email")
    }

    private fun back() {
        finish()
    }

    private fun loadMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }
}