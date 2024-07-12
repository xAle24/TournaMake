package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tournaMake.sampledata.Match

class MatchListViewModel : ViewModel() {
    private val _matchesList = MutableLiveData<List<Match>>()
    val matchesListLiveData : LiveData<List<Match>> = _matchesList

    fun changeMatchesList(list: List<Match>) {
        _matchesList.postValue(list)
    }
}