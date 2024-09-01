package com.example.tournaMake.data.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tournaMake.sampledata.MatchGameData
import com.example.tournaMake.utils.Searchbar

class MatchListViewModel: ViewModel() {
    private val _matchesList = MutableLiveData<List<MatchGameData>>()
    val matchesListLiveData : LiveData<List<MatchGameData>> = _matchesList
    fun changeMatchesList(list: List<MatchGameData>) {
        _matchesList.postValue(list)
    }
}
