package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.MainProfile
import kotlinx.coroutines.launch

/**
 * When fetching data from the database, LiveData should be used.
 * Documentation: https://developer.android.com/topic/libraries/architecture/livedata
 * */
class ProfileViewModel(private val appDatabase: AppDatabase) : ViewModel() {
    private val _profile = MutableLiveData<MainProfile?>()
    val profileLiveData : LiveData<MainProfile?> = _profile

    private val _playedTournaments = MutableLiveData<Int>()
    val playedTournaments: LiveData<Int> = _playedTournaments

    /**
     * This function must be used if you want to update the profile from a coroutineScope, or any
     * scope that isn't the one of the main thread.
     *
     * Source: https://developer.android.com/topic/libraries/architecture/livedata#update_livedata_objects
     * */
    fun changeProfileFromCoroutine(mainProfile: MainProfile?) {
        _profile.postValue(mainProfile)
    }

    fun changePlayedTournaments(playedTournaments: Int) {
        _playedTournaments.postValue(playedTournaments)
    }
}