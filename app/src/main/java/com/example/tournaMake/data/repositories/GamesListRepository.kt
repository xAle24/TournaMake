package com.example.tournaMake.data.repositories

import com.example.tournaMake.sampledata.AppDatabase

class GamesListRepository(private val appDatabase: AppDatabase) {
    fun getAllGames() = appDatabase.gameDao().getAll()
}