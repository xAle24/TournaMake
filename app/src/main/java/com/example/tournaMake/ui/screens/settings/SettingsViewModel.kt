package com.example.tournaMake.ui.screens.settings

import androidx.lifecycle.ViewModel
import com.example.tournaMake.data.models.ThemeEnum
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ThemeState(val theme: ThemeEnum)

/**
 * Create an instance of this ViewModel everywhere you need to see the current application theme.
 * */
class SettingsViewModel : ViewModel() {
    /*
    * Every StateFlow is an Observable that can cause recompositions on all the Components
    * observing it, each time its value changes.
    * The StateFlow must be modified only through the methods of this class; this is why
    * it's a private val.
    * Classes that extend ViewModel are guaranteed to function even in a multi-threading
    * environment, since all operations on the special "value" variable are atomic.
    * */
    private val _state = MutableStateFlow(ThemeState(ThemeEnum.System))
    // This "copy" is a readonly variable for code outside this class
    val state = _state.asStateFlow()

    fun changeTheme(theme: ThemeEnum) {
        _state.value = ThemeState(theme)
    }
}
