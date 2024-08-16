package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tournaMake.sampledata.Game

class MatchCreationViewModel: ViewModel() {
    // The list of games visible when selecting the game played in this match
    private val _gamesList = MutableLiveData<List<Game>>()
    val gamesList: LiveData<List<Game>> = _gamesList

    fun changeGamesList(list: List<Game>) {
        this._gamesList.postValue(list)
    }
}