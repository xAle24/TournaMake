package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.tournaMake.data.repositories.TournamentIDRepository
import com.example.tournaMake.data.repositories.TournamentLiveDataRepository
import com.example.tournaMake.sampledata.MatchTM
import com.example.tournaMake.sampledata.TournamentMatchData

class TournamentDataViewModel(
    private val liveDataRepository: TournamentLiveDataRepository,
    tournamentIDRepository: TournamentIDRepository
): ViewModel() {
    private val _tournamentName = MutableLiveData<String>()
    val tournamentName: LiveData<String> = _tournamentName
    val tournamentID = tournamentIDRepository.tournamentID.asLiveData()

    private val _tournamentMatchesLiveData = MediatorLiveData<List<TournamentMatchData>>()
    val tournamentMatchesAndTeamsLiveData: LiveData<List<TournamentMatchData>> = _tournamentMatchesLiveData

    private val _dbMatchesInTournament = MediatorLiveData<List<MatchTM>>()
    val dbMatchesInTournament: LiveData<List<MatchTM>> = _dbMatchesInTournament

    init {
        _tournamentMatchesLiveData.addSource(tournamentID) {
            if (tournamentID.value != null) {
                val tournamentDataSource = liveDataRepository.getTournamentMatchesLiveData(tournamentID.value!!)
                _tournamentMatchesLiveData.addSource(tournamentDataSource) {
                    _tournamentMatchesLiveData.value = it
                }
            }
        }
        _dbMatchesInTournament.addSource(tournamentID) {
            if (tournamentID.value != null) {
                val dbMatchesSource = liveDataRepository.getMatchesLiveData(tournamentID.value!!)
                _dbMatchesInTournament.addSource(dbMatchesSource) {
                    _dbMatchesInTournament.value = it
                }
            }
        }
    }

    fun refresh(name: String) {
        _tournamentName.postValue(name)
    }
}