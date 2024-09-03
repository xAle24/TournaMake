package com.example.tournaMake.activities

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.ProfileListViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.GuestProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

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
}