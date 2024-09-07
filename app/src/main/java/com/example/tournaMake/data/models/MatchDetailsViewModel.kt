package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.tournaMake.data.repositories.GameDetailsRepository
import com.example.tournaMake.data.repositories.MatchDetailsRepository
import com.example.tournaMake.sampledata.Game
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.sampledata.MatchTM
import com.example.tournaMake.sampledata.Team
import com.example.tournaMake.sampledata.TeamInTm
import com.example.tournaMake.ui.screens.match.TeamUI
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking

/**
 * Given a match, we want to get:
 * - the game that was played
 * - the teams (team name) and team_in_tms (is Winner, score) that played the match
 * - all the main and guest profiles that played in this match (MAIN_PARTICIPANT, GUEST_PARTICIPANT ->
 * MAIN_PROFILE, GUEST_PROFILE)
 * */
class MatchDetailsViewModel(
    repository: MatchDetailsRepository,
    private val gameDetailsRepository: GameDetailsRepository
) : ViewModel() {
    val selectedMatchId = repository.selectedMatch.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    )

    private val _match = MutableLiveData<MatchTM>()
    val match : LiveData<MatchTM> = _match

    private val _selectedGame = gameDetailsRepository.selectedGame.asLiveData()
    val playedGame : LiveData<Game> = _selectedGame.switchMap { gameID ->
        gameID?.let { gameDetailsRepository.getGameDetails(gameID) }
    }

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

    private val _isDraw = MutableLiveData<Boolean>()
    val isDraw: LiveData<Boolean> = _isDraw

    fun changeMatch(match: MatchTM) {
        this._match.postValue(match)
    }

    fun changeRepository(gameID: String) = runBlocking {
        gameDetailsRepository.setSelectedGame(gameID)
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

    fun changeIsDraw(isDraw: Boolean) {
        this._isDraw.postValue(isDraw)
    }
}

/**
 * Stores a TeamUI element associated to other data related to the team's
 * participation in this match.
 * */
data class TeamDataPacket(val teamUI: TeamUI, var teamScore: Int, val teamID: String, val isWinner: Boolean)