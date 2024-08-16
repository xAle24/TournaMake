package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tournaMake.sampledata.Game
import com.example.tournaMake.sampledata.GuestProfile
import com.example.tournaMake.sampledata.MainProfile

class MatchCreationViewModel: ViewModel() {
    // The list of games visible when selecting the game played in this match
    private val _games = MutableLiveData<List<Game>>()
    val games: LiveData<List<Game>> = _games

    fun changeGamesList(list: List<Game>) {
        this._games.postValue(list)
    }

    // List of main profiles
    private val _mainProfiles = MutableLiveData<List<MainProfile>>()
    val mainProfiles: LiveData<List<MainProfile>> = _mainProfiles

    fun changeMainProfiles(list: List<MainProfile>) {
        this._mainProfiles.postValue(list)
    }

    // List of guest profiles
    private val _guestProfiles = MutableLiveData<List<GuestProfile>>()
    val guestProfiles: LiveData<List<GuestProfile>> = _guestProfiles

    fun changeGuestProfiles(list: List<GuestProfile>) {
        this._guestProfiles.postValue(list)
    }
}