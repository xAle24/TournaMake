package com.example.tournaMake.activities

import android.content.Intent
import android.graphics.BitmapFactory
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
import androidx.core.net.toFile
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.LoggedProfileViewModel
import com.example.tournaMake.data.models.ProfileViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.data.repositories.ProfileImageRepository
import com.example.tournaMake.filemanager.AppDirectoryNames
import com.example.tournaMake.filemanager.PROFILE_PICTURE_NAME
import com.example.tournaMake.filemanager.createDirectory
import com.example.tournaMake.filemanager.doesDirectoryContainFile
import com.example.tournaMake.filemanager.doesDirectoryExist
import com.example.tournaMake.filemanager.loadImageUriFromDirectory
import com.example.tournaMake.filemanager.saveImageToDirectory
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.ui.screens.profile.ProfileScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.lang.IllegalStateException

class ProfileActivity : ComponentActivity() {
    private var appDatabase: AppDatabase? = get<AppDatabase>()
    private val profileImageUriRepository: ProfileImageRepository = get<ProfileImageRepository>()

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
            val loggedProfileViewModel = koinViewModel<LoggedProfileViewModel>()
            val loggedEmail = loggedProfileViewModel.loggedEmail.collectAsStateWithLifecycle()
            val profileViewModel = koinViewModel<ProfileViewModel>()
            val profileObserver = Observer<MainProfile?> { profile ->
                Log.d("DEV", "In profile observer profile = ${profile?.email}")
                // TODO: add rest of the profile code
            }
            profileViewModel.profileLiveData.observe(this, profileObserver)
            fetchAndUpdateProfile(loggedEmail.value.loggedProfileEmail, profileViewModel)
            //val profile

            /**
             * TODO: ALIN LEGGI QUI
             * Creare 2 metodi accessibili alla classe screen:
             * 1) un metodo che, se scopre che l'email è già stata caricata nella variabile logged email,
             * fa direttamente il salvataggio della foto nella cartella interna, usando l'email come parametro
             * 2) un altro metodo asincrono che sfrutta un observer, il quale aspetta che l'email sia stata
             * caricata prima di avviare il salvataggio nella cartella interna.
             * Questa activity in qualche modo deve consentire allo screen di vedere lo stato della logged Email.
             * */
            // Adding management of profile photo
            /* Code taken from:
            * https://www.youtube.com/watch?v=uHX5NB6wHao
            * */
            var selectedImageURI by remember {
                mutableStateOf<Uri?>(
                    if(doesDirectoryContainFile(
                        AppDirectoryNames().profileImageDirectoryName,
                        PROFILE_PICTURE_NAME,
                        baseContext,
                        //loggedEmail.value.loggedProfileEmail
                    )) loadImageUriFromDirectory(
                        AppDirectoryNames().profileImageDirectoryName,
                        PROFILE_PICTURE_NAME,
                        baseContext,
                        //loggedEmail.value.loggedProfileEmail
                    ) else null
                )
            }
            val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia(),
                onResult = { uri ->
                    if (uri != null) {
                        storePhotoInInternalStorage(uri, loggedEmail.value.loggedProfileEmail)
                        val uriForInternallySavedFile = loadImageUriFromDirectory(
                            AppDirectoryNames().profileImageDirectoryName,
                            PROFILE_PICTURE_NAME,
                            baseContext,
                            //loggedEmail.value.loggedProfileEmail
                        )
                        if (uriForInternallySavedFile != null) {
                            /* What I want to save in the database is the new uri, not
                            * the one provided by the profile screen. */
                            selectedImageURI = uriForInternallySavedFile
                            uploadPhotoToDatabase(uriForInternallySavedFile, loggedEmail.value.loggedProfileEmail)
                        } else {
                            throw IllegalStateException("The uri received in ProfileActivity.kt is null and can't be saved in the db.")
                        }
                    }
                    Log.d("DEV", "In onResult function in ProfileActivity.kt: everything went fine!")
                }
            )

            ProfileScreen(
                state = state.value,
                /*
                * TODO: consider passing the Observer as a parameter instead of the MainProfile
                *  (forse è una cattiva idea, ma magari si può avere il codice dell'observer
                *  sott'occhio al momento di costruire il ProfileScreen).
                * */
                //profileViewModel.profileLiveData.value
                profileLiveData = profileViewModel.profileLiveData,
                backButton = this::backButton,
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

    private fun storePhotoInInternalStorage(uri: Uri, email: String?) {
        val context = baseContext
        val appDirectoryNames = AppDirectoryNames()
        if (!doesDirectoryExist(appDirectoryNames.profileImageDirectoryName, context, email)) {
            createDirectory(appDirectoryNames.profileImageDirectoryName, context, email)
        }
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        saveImageToDirectory(
            bitmap = bitmap,
            context = context,
            dirName = appDirectoryNames.profileImageDirectoryName, // defined in filemanager/FileUtils.kt
            imageName = PROFILE_PICTURE_NAME, // defined in filemanager/FileUtils.kt
            //email = email
        )
    }

    private fun fetchAndUpdateProfile(email: String, profileViewModel: ProfileViewModel) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val myProfile = appDatabase?.mainProfileDao()?.getProfileByEmail(email)
                Log.d("DEV", "In getProfile() coroutine, myProfile.email = ${myProfile?.email}")
                // Now update the data in the view model, to trigger the onchange method of the attached
                // observer
                profileViewModel.changeProfileFromCoroutine(myProfile)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun backButton() {
        finish()
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