package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tournaMake.sampledata.Game
import com.example.tournaMake.sampledata.Match
import com.example.tournaMake.sampledata.PlayedGame

class GraphViewModel : ViewModel() {
    private val _matchList = MutableLiveData<List<Match>>()
    val matchListLiveData : LiveData<List<Match>> = _matchList

    private val _gamesMap = MutableLiveData<List<PlayedGame>>()
    val gamesListLiveData : LiveData<List<PlayedGame>> = _gamesMap

    fun changeMatchList(list: List<Match>) {
        _matchList.postValue(list)
    }
    fun changeGamesList(gamesMap: List<PlayedGame>) {
        _gamesMap.postValue(gamesMap)
    }
}