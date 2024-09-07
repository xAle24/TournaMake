package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.tournaMake.data.repositories.MatchRepository
import com.example.tournaMake.sampledata.MatchGameData
import kotlinx.coroutines.runBlocking

class MatchListViewModel(private val repository: MatchRepository): ViewModel() {
    val allMatchesListLiveData : LiveData<List<MatchGameData>> = repository.getAllMatchesListLiveData()

    private val _loggedEmail = MutableLiveData<String>()
    val loggedPlayerMatchesHistory = _loggedEmail.switchMap { email ->
        repository.getPlayerHistory(email)
    }

    suspend fun changeRepository(matchID: String) = runBlocking {
        repository.setSelectedMatch(matchID)
    }

    fun changeEmail(email: String) {
        _loggedEmail.postValue(email)
    }
}
