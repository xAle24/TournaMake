package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournaMake.data.repositories.MatchRepository
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.sampledata.MatchTM
import com.example.tournaMake.sampledata.Team
import com.example.tournaMake.sampledata.TeamInTm
import com.example.tournaMake.ui.screens.match.TeamUI
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class MatchViewModel(private val repository: MatchRepository) : ViewModel() {
    val selectedMatchId = repository.selectedMatch.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    )

    private val _match = MutableLiveData<MatchTM>()
    val match : LiveData<MatchTM> = _match

    private val _teams = MutableLiveData<List<Team>>()
    val teams : LiveData<List<Team>> = _teams

    private val _teamsInMatch = MutableLiveData<List<TeamInTm>>()

    private val _mainProfiles = MutableLiveData<List<MainProfile>>()
    val mainProfiles : LiveData<List<MainProfile>> = _mainProfiles

    /**
     * Maybe the other fields are useless.
     * */
    private val _teamUIs = MutableLiveData<List<TeamUI>>()

    private val _teamDataPackets = MutableLiveData<List<TeamDataPacket>>()
    val teamDataPackets: LiveData<List<TeamDataPacket>> = _teamDataPackets

    suspend fun changeRepository(matchID: String){
        repository.setSelectedMatch(matchID)
    }

    fun changeMatch(match: MatchTM) {
        this._match.postValue(match)
    }

    fun changeTeams(teams: List<Team>) {
        this._teams.postValue(teams)
    }

    fun changeTeamsInMatch(teamsInTm: List<TeamInTm>) {
        this._teamsInMatch.postValue(teamsInTm)
    }

    fun changeTeamUIs(teamUIs: List<TeamUI>) {
        this._teamUIs.postValue(teamUIs)
    }

    fun changeTeamDataPackets(teamDataPackets: List<TeamDataPacket>) {
        this._teamDataPackets.postValue(teamDataPackets)
    }
}