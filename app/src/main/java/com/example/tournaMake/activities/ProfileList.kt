package com.example.tournaMake.activities

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.GuestProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
/*
fun fetchAndUpdateGuestProfiles(profileListViewModel: ProfileListViewModel, owner: LifecycleOwner) {
    val appDatabase = inject<AppDatabase>(AppDatabase::class.java)
    var guestProfiles: List<GuestProfile>
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            guestProfiles = appDatabase.value.guestProfileDao().getAll()
            Log.d("DEV", "In fetchAndUpdateGuestProfile() in ProfileListActivity, guestProfiles = ${guestProfiles.forEach{ profile -> profile.username}}")
            profileListViewModel.changeProfileNamesList(guestProfiles.map { it.username })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}*/

fun createGuestProfile(
    email: String,
    owner: LifecycleOwner,
    context: Context
) {
    val appDatabase = inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            if (appDatabase.value.guestProfileDao().checkGuestProfile(email) == 1) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Profile already exists", Toast.LENGTH_SHORT).show()
                }
            } else {
                appDatabase.value.guestProfileDao().insert(GuestProfile(email))
                withContext(Dispatchers.Main){
                    Toast.makeText(context, "Profile created", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}