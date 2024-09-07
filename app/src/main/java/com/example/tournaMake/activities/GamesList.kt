package com.example.tournaMake.activities

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.GamesListViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

fun fetchAndUpdateGameList(gamesListViewModel: GamesListViewModel, owner: LifecycleOwner) {
    val appDatabase = inject<AppDatabase>(AppDatabase::class.java)
    var gamesList: List<Game>
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            gamesList = appDatabase.value.gameDao().getAll()
            gamesListViewModel.changeGameList(gamesList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
fun addGame(game: Game, owner: LifecycleOwner) {
    val appDatabase = inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            appDatabase.value.gameDao().insertAll(game)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
fun addGameToFavorites(gameID: String, owner: LifecycleOwner) {
    val db = inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            db.value.gameDao().setGameFavorites(gameID)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
fun removeGameFromFavorites(gameID: String, owner: LifecycleOwner) {
    val db = inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            db.value.gameDao().removeGameFavorites(gameID)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}