package com.example.tournaMake.data.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tournaMake.data.repositories.GamesListRepository
import com.example.tournaMake.sampledata.Game
import com.example.tournaMake.sampledata.GuestProfile
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.ui.screens.match.TeamUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MatchCreationViewModel(repository: GamesListRepository): ViewModel() {
    // The list of games visible when selecting the game played in this match
    val games: LiveData<List<Game>> = repository.getAllGames()

    // List of main profiles
    private val _mainProfiles = MutableLiveData<List<MainProfile>>()
    val mainProfiles: LiveData<List<MainProfile>> = _mainProfiles

    fun changeMainProfiles(list: List<MainProfile>) {
        this._mainProfiles.postValue(list)
    }

    // List of guest profiles
    private val _guestProfiles = MutableLiveData<List<GuestProfile>>()

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

    fun removeTeam(team: TeamUI) {
        _teamsSet.value = _teamsSet.value.filter { it != team }.toSet()
        Log.d("DEV-MATCH-CREATION", "Content of teams set after deletion: ${_teamsSet.value}")
    }

    private val _selectedMainProfiles: MutableLiveData<Set<MainProfile>> = MutableLiveData(mutableSetOf())
    val selectedMainProfiles: LiveData<Set<MainProfile>> = _selectedMainProfiles
    private val _selectedGuestProfiles: MutableLiveData<Set<GuestProfile>> = MutableLiveData(mutableSetOf())
    val selectedGuestProfiles: LiveData<Set<GuestProfile>> = _selectedGuestProfiles

    /**
     * Returns a collection containing all the members that are not currently
     * selected.
     * */
    fun filterUnselectedMainMembers(mainProfiles: List<MainProfile>): List<MainProfile> {
        return _selectedMainProfiles.value?.let {
            mainProfiles.filter {
                !this._selectedMainProfiles.value!!.contains(it)
            }
        } ?: mainProfiles
    }

    /**
     * Returns a collection containing all the members that are not currently
     * selected.
     * */
    fun filterUnselectedGuestMembers(guestProfiles: List<GuestProfile>): List<GuestProfile> {
        return _selectedGuestProfiles.value?.let {
            guestProfiles.filter {
                !this._selectedGuestProfiles.value!!.contains(it)
            }
        } ?: guestProfiles
    }

    fun addMain(profile: MainProfile) {
        this._selectedMainProfiles.postValue(_selectedMainProfiles.value?.plus(profile))
    }

    fun removeMain(profile: MainProfile) {
        this._selectedMainProfiles.postValue(_selectedMainProfiles.value?.minus(profile))
    }

    fun addGuest(profile: GuestProfile) {
        this._selectedGuestProfiles.postValue(_selectedGuestProfiles.value?.plus(profile))
    }

    fun removeGuest(profile: GuestProfile) {
        this._selectedGuestProfiles.postValue(_selectedGuestProfiles.value?.minus(profile))
    }
}