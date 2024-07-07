package com.example.tournaMake.data.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournaMake.data.repositories.ThemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ThemeState(val theme: ThemeEnum)

/**
 * Create an instance of this ViewModel everywhere you need to see the current application theme.
 * */
class ThemeViewModel(private val repository: ThemeRepository) : ViewModel() {
    val state = repository.theme.map { ThemeState(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ThemeState(ThemeEnum.System)
    )
    fun changeTheme(theme: ThemeEnum) = viewModelScope.launch {
        repository.setTheme(theme)
    }
}
