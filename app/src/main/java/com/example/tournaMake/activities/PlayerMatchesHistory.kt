package com.example.tournaMake.activities

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.MatchListViewModel
import com.example.tournaMake.sampledata.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

fun fetchAndUpdateMatch(email: String, matchListViewModel: MatchListViewModel, owner: LifecycleOwner) {
    val appDatabase = inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            val myMatch = appDatabase.value.matchDao().getMyMatches(email)
            Log.d("DEV", "In getProfile() coroutine, myProfile.email = $myMatch")
            // Now update the data in the view model, to trigger the onchange method of the attached
            // observer
            matchListViewModel.changeMatchesList(myMatch)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
