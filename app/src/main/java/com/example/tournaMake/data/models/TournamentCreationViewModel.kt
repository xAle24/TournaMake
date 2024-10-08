package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tournaMake.data.repositories.GamesListRepository
import com.example.tournaMake.data.repositories.GuestProfileRepository
import com.example.tournaMake.sampledata.Game
import com.example.tournaMake.sampledata.GuestProfile
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.sampledata.TournamentType

class TournamentCreationViewModel(repository: GamesListRepository, guestProfileRepository: GuestProfileRepository) : ViewModel() {
    val gamesListLiveData : LiveData<List<Game>> = repository.getAllGames()

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

    val guestProfileListLiveData : LiveData<List<GuestProfile>> = guestProfileRepository.getAllGuestProfile()
}