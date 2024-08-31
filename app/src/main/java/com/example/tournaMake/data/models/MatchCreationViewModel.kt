package com.example.tournaMake.data.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tournaMake.sampledata.Game
import com.example.tournaMake.sampledata.GuestProfile
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.ui.screens.match.TeamUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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

    // Management of user data
    private val _teamsSet = MutableStateFlow<Set<TeamUI>>(setOf())
    val teamsSet: StateFlow<Set<TeamUI>> = _teamsSet

    // Takes teams from UI data
    fun addTeam(team: TeamUI) {
        _teamsSet.value = setOf(setOf(team), _teamsSet.value).flatten().toSet()
        Log.d("DEV-MATCH-CREATION", "Content of teams set after addition: ${_teamsSet.value}")
    }

    /*fun addMemberToTeam(team: TeamUI, profile: MainProfile) {
        val indexOfTeam = this._teamsSet.value.indexOf(team)
        this._teamsSet.value.elementAt(indexOfTeam).addMainProfile(profile)
    }

    fun addMemberToTeam(team: TeamUI, profile: GuestProfile) {
        val indexOfTeam = this._teamsSet.value.indexOf(team)
        this._teamsSet.value.elementAt(indexOfTeam).addGuestProfile(profile)
    }

    fun removeMemberFromTeam(team: TeamUI, profile: MainProfile) {
        val indexOfTeam = this._teamsSet.value.indexOf(team)
        this._teamsSet.value.elementAt(indexOfTeam).removeMainProfile(profile)
    }

    fun removeMemberFromTeam(team: TeamUI, profile: GuestProfile) {
        val indexOfTeam = this._teamsSet.value.indexOf(team)
        this._teamsSet.value.elementAt(indexOfTeam).removeGuestProfile(profile)
    }*/

    fun removeTeam(team: TeamUI) {
        _teamsSet.value = _teamsSet.value.filter { it != team }.toSet()
        Log.d("DEV-MATCH-CREATION", "Content of teams set after deletion: ${_teamsSet.value.toString()}")
    }
}