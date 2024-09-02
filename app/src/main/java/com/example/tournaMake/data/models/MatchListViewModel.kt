package com.example.tournaMake.data.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournaMake.data.repositories.MatchRepository
import com.example.tournaMake.sampledata.MatchGameData
import com.example.tournaMake.utils.Searchbar
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class MatchListViewModel(private val repository: MatchRepository): ViewModel() {
    val selectedMatchId = repository.selectedMatch.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    )

    private val _matchesList = MutableLiveData<List<MatchGameData>>()
    val matchesListLiveData : LiveData<List<MatchGameData>> = _matchesList

    fun changeMatchesList(list: List<MatchGameData>) {
        _matchesList.postValue(list)
    }

    suspend fun changeRepository(matchID: String){
        repository.setSelectedMatch(matchID)
    }
}
