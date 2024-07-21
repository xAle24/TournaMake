package com.example.tournaMake.data.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournaMake.data.repositories.TournamentIDRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TournamentIDViewModel(private val repository: TournamentIDRepository): ViewModel() {
    val tournamentID = repository.tournamentID.map { it.toString() }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ""
    )

    fun saveTournamentIDInPreferences(id: String) = viewModelScope.launch {
        repository.setTournamentID(id)
    }
}