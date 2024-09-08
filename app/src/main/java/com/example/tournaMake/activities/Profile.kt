
package com.example.tournaMake.activities

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.AchievementsProfileViewModel
import com.example.tournaMake.data.models.ProfileViewModel
import com.example.tournaMake.filemanager.AppDirectoryNames
import com.example.tournaMake.filemanager.PROFILE_PICTURE_NAME
import com.example.tournaMake.filemanager.loadImageUriFromDirectory
import com.example.tournaMake.sampledata.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
fun uploadPhotoToDatabase(uri: Uri, loggedEmail: String) {
    Log.d("DEV", "Got logged email: $loggedEmail")
    // TODO: add database uri uploading
}
/**
 * Fetches data from database, mainly the profile email and location.
 * TODO: add number of played tournaments
 * */
fun fetchAndUpdateProfile(
    email: String,
    profileViewModel: ProfileViewModel,
    thenUpdateImageUri: (Uri?) -> Unit,
    baseContext: Context,
    owner: LifecycleOwner
) {
    val appDatabase = inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            val myProfile = appDatabase.value.mainProfileDao().getProfileByEmail(email)
            val playedTournaments = appDatabase.value.mainProfileDao().getAllTournamentsPlayed(email)
            Log.d("DEV", "In getProfile() coroutine, myProfile.email = ${myProfile.email}")
            // Now update the data in the view model, to trigger the onchange method of the attached
            // observer
            profileViewModel.changeProfileFromCoroutine(myProfile)
            profileViewModel.changePlayedTournaments(playedTournaments)
            val profileImageUri = loadImageUriFromDirectory(
                dirName = AppDirectoryNames.profileImageDirectoryName,
                imageName = PROFILE_PICTURE_NAME,
                context = baseContext,
                email = myProfile.email
            )
            thenUpdateImageUri(profileImageUri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
fun fetchAndUpdateAchievementsProfile(
    email: String,
    achievementsProfileViewModel: AchievementsProfileViewModel,
    owner: LifecycleOwner
){
    val appDatabase = inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            val myAchievement = appDatabase.value.achievementPlayerDao().getAchievementsByEmail(email)
            achievementsProfileViewModel.updateAchievementProfileList(myAchievement)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
