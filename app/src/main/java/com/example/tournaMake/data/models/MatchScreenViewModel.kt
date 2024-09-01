package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournaMake.data.repositories.MatchRepository
import com.example.tournaMake.sampledata.Game
import com.example.tournaMake.sampledata.GuestParticipant
import com.example.tournaMake.sampledata.GuestProfile
import com.example.tournaMake.sampledata.MainParticipant
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.sampledata.MatchTM
import com.example.tournaMake.sampledata.Team
import com.example.tournaMake.sampledata.TeamInTm
import com.example.tournaMake.ui.screens.match.TeamUI
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class MatchScreenViewModel(private val repository: MatchRepository) : ViewModel() {
    val selectedMatchId = repository.selectedMatch.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ""
    )

    private val _match = MutableLiveData<MatchTM>()
    val match : LiveData<MatchTM> = _match

    private val _playedGame = MutableLiveData<Game>()
    val playedGame : LiveData<Game> = _playedGame

    private val _teams = MutableLiveData<List<Team>>()
    val teams : LiveData<List<Team>> = _teams

    private val _teamsInMatch = MutableLiveData<List<TeamInTm>>()
    val teamsInMatch : LiveData<List<TeamInTm>> = _teamsInMatch

    private val _mainProfiles = MutableLiveData<List<MainProfile>>()
    val mainProfiles : LiveData<List<MainProfile>> = _mainProfiles

    private val _guestProfiles = MutableLiveData<List<GuestProfile>>()
    val guestProfiles : LiveData<List<GuestProfile>> = _guestProfiles

    private val _mainParticipants = MutableLiveData<List<MainParticipant>>()
    val mainParticipants : LiveData<List<MainParticipant>> = _mainParticipants

    private val _guestParticipants = MutableLiveData<List<GuestParticipant>>()
    val guestParticipants : LiveData<List<GuestParticipant>> = _guestParticipants

    /**
     * Maybe the other fields are useless.
     * */
    private val _teamUIs = MutableLiveData<List<TeamUI>>()
    val teamUIs : LiveData<List<TeamUI>> = _teamUIs

    private val _teamDataPackets = MutableLiveData<List<TeamDataPacket>>()
    val teamDataPackets: LiveData<List<TeamDataPacket>> = _teamDataPackets

    suspend fun changeRepository(matchID: String){
        repository.setSelectedMatch(matchID)
    }

    fun changeMatch(match: MatchTM) {
        this._match.postValue(match)
    }

    fun changePlayedGame(game: Game) {
        this._playedGame.postValue(game)
    }

    fun changeTeams(teams: List<Team>) {
        this._teams.postValue(teams)
    }

    fun changeTeamsInMatch(teamsInTm: List<TeamInTm>) {
        this._teamsInMatch.postValue(teamsInTm)
    }

    fun changeMainProfiles(profiles: List<MainProfile>) {
        this._mainProfiles.postValue(profiles)
    }

    fun changeMainParticipants(participations: List<MainParticipant>) {
        this._mainParticipants.postValue(participations)
    }

    fun changeGuestProfiles(profiles: List<GuestProfile>) {
        this._guestProfiles.postValue(profiles)
    }

    fun changeGuestParticipants(participations: List<GuestParticipant>) {
        this._guestParticipants.postValue(participations)
    }

    fun changeTeamUIs(teamUIs: List<TeamUI>) {
        this._teamUIs.postValue(teamUIs)
    }

    fun changeTeamDataPackets(teamDataPackets: List<TeamDataPacket>) {
        this._teamDataPackets.postValue(teamDataPackets)
    }
}