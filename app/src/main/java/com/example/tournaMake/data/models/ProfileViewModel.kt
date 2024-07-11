package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tournaMake.sampledata.MainProfile

class ProfileViewModel() : ViewModel() {
    private val _profile = MutableLiveData<MainProfile?>()
    val profileLiveData : LiveData<MainProfile?> = _profile

    fun changeProfile(mainProfile: MainProfile?) {
        _profile.value= mainProfile
    }

    fun changeProfileFromCoroutine(mainProfile: MainProfile?) {
        _profile.postValue(mainProfile)
    }
}