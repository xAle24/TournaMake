package com.example.tournaMake.activities

import android.content.Intent
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
import com.example.tournaMake.ui.screens.registration.RegistrationPhotoScreen
import org.koin.androidx.compose.koinViewModel

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

    private fun back() {
        finish()
    }

    private fun loadMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }
}