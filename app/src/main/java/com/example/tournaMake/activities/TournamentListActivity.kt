package com.example.tournaMake.activities

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.TournamentListViewModel
import com.example.tournaMake.sampledata.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

fun fetchAndUpdateTournament(vm: TournamentListViewModel, owner: LifecycleOwner) {
    val db = inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch (Dispatchers.IO) {
        try {
            val tournaments = db.value.tournamentDao().getAll()
            vm.changeTournamentList(tournaments)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}