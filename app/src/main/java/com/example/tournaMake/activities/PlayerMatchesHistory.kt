package com.example.tournaMake.activities

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.MatchListViewModel
import com.example.tournaMake.sampledata.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

fun fetchAndUpdateMatch(email: String, matchListViewModel: MatchListViewModel) {
    matchListViewModel.changeEmail(email)
}
