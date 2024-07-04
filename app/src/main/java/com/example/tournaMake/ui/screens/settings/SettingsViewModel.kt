package com.example.tournaMake.ui.screens.settings

import androidx.lifecycle.ViewModel
import com.example.tournaMake.ui.theme.Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ThemeState(val theme: Theme)

class SettingsViewModel : ViewModel() {
    private val _state = MutableStateFlow(ThemeState(Theme.System))
    val state = _state.asStateFlow()

    fun changeTheme(theme: Theme) {
        _state.value = ThemeState(theme)
    }
}
