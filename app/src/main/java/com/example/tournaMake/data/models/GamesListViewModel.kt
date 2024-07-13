package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tournaMake.sampledata.Game
class GamesListViewModel : ViewModel() {
    private val _gamesList = MutableLiveData<List<Game>>()
    val gamesListLiveData : LiveData<List<Game>> = _gamesList

    fun changeGameList(list: List<Game>) {
        _gamesList.postValue(list)
    }
}