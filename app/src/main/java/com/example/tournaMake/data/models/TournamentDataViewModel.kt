package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tournaMake.sampledata.TournamentMatchData

class TournamentDataViewModel: ViewModel() {
    private val _tournamentData = MutableLiveData<List<TournamentMatchData>>()
    val tournamentMatchesAndTeamsLiveData : LiveData<List<TournamentMatchData>> = _tournamentData
    private val _tournamentName = MutableLiveData<String>()
    val tournamentName: LiveData<String> = _tournamentName

    fun changeMatchesList(list: List<TournamentMatchData>) {
        _tournamentData.postValue(list)
    }

    fun changeTournamentName(name: String) {
        _tournamentName.postValue(name)
    }
}