package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tournaMake.sampledata.Match

class MatchListViewModel : ViewModel() {
    private val _matchList = MutableLiveData<List<Match>?>()
    val matchListLiveData : LiveData<List<Match>?> = _matchList

    fun changeMatchList(list: List<Match>?) {
        _matchList.postValue(list)
    }
}