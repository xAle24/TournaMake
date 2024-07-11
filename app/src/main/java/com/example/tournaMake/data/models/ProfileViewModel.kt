package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tournaMake.sampledata.MainProfile

/**
 * When fetching data from the database, LiveData should be used.
 * Documentation: https://developer.android.com/topic/libraries/architecture/livedata
 * */
class ProfileViewModel() : ViewModel() {
    private val _profile = MutableLiveData<MainProfile?>()
    val profileLiveData : LiveData<MainProfile?> = _profile

    fun changeProfile(mainProfile: MainProfile?) {
        _profile.value= mainProfile
    }

    /**
     * This function must be used if you want to update the profile from a coroutineScope, or any
     * scope that isn't the one of the main thread.
     *
     * Source: https://developer.android.com/topic/libraries/architecture/livedata#update_livedata_objects
     * */
    fun changeProfileFromCoroutine(mainProfile: MainProfile?) {
        _profile.postValue(mainProfile)
    }
}