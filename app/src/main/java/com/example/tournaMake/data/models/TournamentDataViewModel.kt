package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.MatchTM
import com.example.tournaMake.sampledata.TournamentMatchData
import org.koin.java.KoinJavaComponent.inject

class TournamentDataViewModel: ViewModel() {
    val appDatabase = inject<AppDatabase>(AppDatabase::class.java)

    private val _tournamentName = MutableLiveData<String>()
    val tournamentName: LiveData<String> = _tournamentName
    private val _tournamentID = MutableLiveData<String>()
    val tournamentID: LiveData<String> = _tournamentID
    var tournamentMatchesAndTeamsLiveData: LiveData<List<TournamentMatchData>> = MutableLiveData()
    private set
    var dbMatchesInTournament: LiveData<List<MatchTM>> = MutableLiveData()
    private set

    fun changeMatchesList(list: List<TournamentMatchData>) {
        //_tournamentData.postValue(list)
        // TODO: eliminate dependencies and then this method

    }

    fun refresh(name: String, tournamentID: String) {
        _tournamentName.postValue(name)
        _tournamentID.postValue(tournamentID)
        tournamentMatchesAndTeamsLiveData = appDatabase.value
            .tournamentDao()
            .getTournamentMatchLiveData(tournamentID)
        dbMatchesInTournament = appDatabase.value
            .matchDao()
            .getMatchesInTournament(tournamentID)
    }
}