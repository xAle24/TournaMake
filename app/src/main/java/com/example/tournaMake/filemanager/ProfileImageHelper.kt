package com.example.tournaMake.filemanager

import android.content.ContentResolver
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.datastore.core.IOException
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.tournaMake.data.models.LoggedProfileState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * An interface to model an object that handles profile pictures.
 * */
interface ProfileImageHelper {
    /**
     * Stores the profile picture without waiting for a certain condition.
     * The default location is context.filesDir/AppDirectoryNames.profileImageDirectoryName/email.
     * */
    fun storeProfilePictureImmediately(
        profileImageUri: Uri,
        email: String,
        context: Context,
        contentResolver: ContentResolver,
        databaseUpdaterCallback: (Uri, String) -> Unit
    ): Uri

    fun waitForEmailThenStoreProfilePicture(
        loggedEmailStateFlow: StateFlow<LoggedProfileState>,
        profileImageUri: Uri,
        stateChangerCallback: (Uri?) -> Unit,
        databaseUpdaterCallback: (Uri, String) -> Unit,
        lifecycleCoroutineScope: LifecycleCoroutineScope,
        lifecycleOwner: LifecycleOwner,
        context: Context,
        contentResolver: ContentResolver,
    )
}

class ProfileImageHelperImpl: ProfileImageHelper {
    /**
     * If the email is already present, it can be used directly to create the user's specific
     * folder. This method is needed because an already present email cannot be waited for
     * with the previous method [waitForEmailThenStoreProfilePicture], otherwise the waiting
     * coroutine would probably be stuck forever.
     * */
    override fun storeProfilePictureImmediately(
        profileImageUri: Uri,
        email: String,
        context: Context,
        contentResolver: ContentResolver,
        databaseUpdaterCallback: (Uri, String) -> Unit
    ): Uri {
        this.storePhotoInInternalStorage(
            profileImageUri,
            email,
            context,
            contentResolver
        )
        val newUri = loadImageUriFromDirectory(
            AppDirectoryNames.profileImageDirectoryName,
            PROFILE_PICTURE_NAME,
            context,
            email
        )
        if (newUri != null) {
            databaseUpdaterCallback(newUri, email)
        } else {
            throw IOException("No uri was found! Can't update database!")
        }
        return newUri
    }

    /**
     * If I understood correctly, this function collects the firs emission of the
     * state flow. This happens each time the loggedEmailStateFlow changes and the lifecycle
     * of the activity is in the STARTED state.
     * Code taken from the official documentation at:
     * https://developer.android.com/kotlin/flow/stateflow-and-sharedflow
     * */
    override fun waitForEmailThenStoreProfilePicture(
        loggedEmailStateFlow: StateFlow<LoggedProfileState>,
        profileImageUri: Uri,
        stateChangerCallback: (Uri?) -> Unit,
        databaseUpdaterCallback: (Uri, String) -> Unit,
        lifecycleCoroutineScope: LifecycleCoroutineScope,
        lifecycleOwner: LifecycleOwner,
        context: Context,
        contentResolver: ContentResolver,
    ) {
        lifecycleCoroutineScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loggedEmailStateFlow.collect { loggedProfileState ->
                    storePhotoInInternalStorage(
                        profileImageUri,
                        loggedProfileState.loggedProfileEmail,
                        context,
                        contentResolver
                    )
                    // Now that the picture is uploaded, we can retrieve its location as uri
                    val uriForInternallySavedFile = loadImageUriFromDirectory(
                        AppDirectoryNames.profileImageDirectoryName,
                        PROFILE_PICTURE_NAME,
                        context,
                        loggedEmailStateFlow.value.loggedProfileEmail
                    )
                    // Updating the database
                    if (uriForInternallySavedFile != null) {
                        databaseUpdaterCallback(uriForInternallySavedFile, loggedProfileState.loggedProfileEmail)
                    } else {
                        throw IOException("No uri was found! Can't update database!")
                    }
                    Log.d(
                        "DEV",
                        "In coroutine inside waitForEmailThenStoreProfilePicture() " +
                                "in ProfileImageHelperImpl.kt, uriForInternallySavedFile is " +
                                "$uriForInternallySavedFile"
                    )
                    stateChangerCallback(uriForInternallySavedFile)
                }
            }
        }
    }

    private fun storePhotoInInternalStorage(
        uri: Uri,
        email: String?,
        context: Context,
        contentResolver: ContentResolver
    ) {
        if (!doesDirectoryExist(AppDirectoryNames.profileImageDirectoryName, context, email)) {
            createDirectory(AppDirectoryNames.profileImageDirectoryName, context, email)
            Log.d("DEV", "storePhotoInInternalStorage - Created directory!")
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
    }
}
fun storePhotoInInternalStorage(
    uri: Uri,
    email: String?,
    context: Context,
    contentResolver: ContentResolver
) {
    if (!doesDirectoryExist(AppDirectoryNames.profileImageDirectoryName, context, email)) {
        createDirectory(AppDirectoryNames.profileImageDirectoryName, context, email)
        Log.d("DEV", "storePhotoInInternalStorage - Created directory!")
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
}
