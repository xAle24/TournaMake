package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tournaMake.data.repositories.GamesListRepository
import com.example.tournaMake.sampledata.Game
class GamesListViewModel(repository: GamesListRepository) : ViewModel() {
    val gamesListLiveData : LiveData<List<Game>> = repository.getAllGames()
}