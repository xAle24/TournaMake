package com.example.tournaMake.data.repositories

import com.example.tournaMake.sampledata.AppDatabase
import org.koin.java.KoinJavaComponent.inject

class TournamentLiveDataRepository {
    private val appDatabase = inject<AppDatabase>(AppDatabase::class.java)

    fun getTournamentMatchesLiveData(tournamentID: String) =
        appDatabase.value.tournamentDao().getTournamentMatchLiveData(tournamentID)

    fun getMatchesLiveData(tournamentID: String) =
        appDatabase.value.matchDao().getMatchesInTournament(tournamentID)
}