package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tournaMake.sampledata.Game
import com.example.tournaMake.sampledata.GuestProfile
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.sampledata.TournamentType

class TournamentCreationViewModel : ViewModel() {
    private val _gamesList = MutableLiveData<List<Game>>()
    val gamesListLiveData : LiveData<List<Game>> = _gamesList

    fun changeGameList(list: List<Game>) {
        _gamesList.postValue(list)
    }

    private val _tournamentTypeList = MutableLiveData<List<TournamentType>>()
    val tournamentListLiveData : LiveData<List<TournamentType>> = _tournamentTypeList

    fun changeTournamentTypeList(list: List<TournamentType>) {
        _tournamentTypeList.postValue(list)
    }

    private val _mainProfileList = MutableLiveData<List<MainProfile>>()
    val mainProfileListLiveData : LiveData<List<MainProfile>> = _mainProfileList

    fun changeMainProfileList(list: List<MainProfile>) {
        _mainProfileList.postValue(list)
    }

    private val _guestProfileList = MutableLiveData<List<GuestProfile>>()
    val guestProfileListLiveData : LiveData<List<GuestProfile>> = _guestProfileList

    fun changeGuestProfileList(list: List<GuestProfile>) {
        _guestProfileList.postValue(list)
    }
}