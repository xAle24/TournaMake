package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileListViewModel : ViewModel() {
    private val _profileNamesList = MutableLiveData<List<String>>()
    val profileNamesListLiveData : LiveData<List<String>> = _profileNamesList

    fun changeProfileNamesList(list: List<String>) {
        _profileNamesList.postValue(list)
    }
}