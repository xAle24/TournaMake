package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tournaMake.sampledata.MatchGameData

class MatchListViewModel: ViewModel() {
    private val _matchesList = MutableLiveData<List<MatchGameData>>()
    val matchesListLiveData : LiveData<List<MatchGameData>> = _matchesList

    fun changeMatchesList(list: List<MatchGameData>) {
        _matchesList.postValue(list)
    }
}
