package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournaMake.data.repositories.MatchDetailsRepository
import com.example.tournaMake.sampledata.Game
import com.example.tournaMake.sampledata.GuestParticipant
import com.example.tournaMake.sampledata.GuestProfile
import com.example.tournaMake.sampledata.MainParticipant
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.sampledata.Team
import com.example.tournaMake.sampledata.TeamInTm
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Given a match, we want to get:
 * - the game that was played
 * - the teams (team name) and team_in_tms (is Winner, score) that played the match
 * - all the main and guest profiles that played in this match (MAIN_PARTICIPANT, GUEST_PARTICIPANT ->
 * MAIN_PROFILE, GUEST_PROFILE)
 * */
class MatchDetailsViewModel(private val repository: MatchDetailsRepository) : ViewModel() {
    val selectedMatchId = repository.selectedMatch.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ""
    )

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
}