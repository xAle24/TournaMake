package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tournaMake.sampledata.Tournament

class TournamentListViewModel : ViewModel() {
    private val _tournamentList = MutableLiveData<List<Tournament>>()
    val tournamentListLiveData : LiveData<List<Tournament>> = _tournamentList

    fun changeTournamentList(list: List<Tournament>) {
        _tournamentList.postValue(list)
    }
}