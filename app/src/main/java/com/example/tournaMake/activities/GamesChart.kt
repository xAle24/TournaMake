package com.example.tournaMake.activities

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.GraphViewModel
import com.example.tournaMake.sampledata.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
fun fetchAndUpdateGraph(email: String, graphViewModel: GraphViewModel, owner: LifecycleOwner) {
    val appDatabase = inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            val myGames = appDatabase.value.gameDao().getPlayedGames(email)
            Log.d("DEV", "In GamesChart.kt, myGames = $myGames")
            graphViewModel.changeGamesList(myGames)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
