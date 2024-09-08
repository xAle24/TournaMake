package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.example.tournaMake.data.repositories.GameDetailsRepository
import com.example.tournaMake.sampledata.Game
import com.example.tournaMake.sampledata.GameHighScores
import kotlinx.coroutines.runBlocking

class GameDetailsViewModel(private val repository: GameDetailsRepository) : ViewModel() {
    private val _gameID = repository.selectedGame.asLiveData()

    val gameDetailsListLiveData : LiveData<Game> = _gameID.switchMap { gameID ->
        gameID?.let { repository.getGameDetails(it) }
    }
    val gameHighScoresMain : LiveData<List<GameHighScores>> = _gameID.switchMap { gameID ->
        gameID?.let { repository.getMainHighScores(it) }
    }
    val gameHighScoresGuest : LiveData<List<GameHighScores>> = _gameID.switchMap { gameID ->
        gameID?.let { repository.getGuestHighScores(it) }
    }

    fun changeRepository(gameID: String) = runBlocking {
        repository.setSelectedGame(gameID)
    }
}