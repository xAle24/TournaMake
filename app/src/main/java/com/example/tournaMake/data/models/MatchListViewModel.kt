package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tournaMake.sampledata.MatchTM

class MatchListViewModel: ViewModel() {
    private val _matchesList = MutableLiveData<List<MatchTM>>()
    val matchesListLiveData : LiveData<List<MatchTM>> = _matchesList

    fun changeMatchesList(list: List<MatchTM>) {
        _matchesList.postValue(list)
    }
}
