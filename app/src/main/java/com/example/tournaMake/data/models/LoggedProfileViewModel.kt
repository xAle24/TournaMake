package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournaMake.data.repositories.LoggedProfileRepository
import com.example.tournaMake.sampledata.MainProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// To keep track of the currently logged in or registered user
// This email refers to a MainProfile
data class LoggedProfileState(val loggedProfileEmail: String)

class LoggedProfileViewModel(private val repository: LoggedProfileRepository) : ViewModel() {
    val loggedEmail = repository.email.map { LoggedProfileState(it.toString()) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = LoggedProfileState("")
    )
    private val _profile = MutableLiveData<MainProfile?>(null)
    val profileLiveData: LiveData<MainProfile?> = _profile
    fun setAndSaveEmail(email: String) = viewModelScope.launch {
        repository.setEmail(email)
    }
    /*
    Use loggedEmailTemp if the user does not want the application to remember their
    credentials when closed.*
    The backing property _loggedEmailTemp is mutable, and thus must not be accessed by
    external code.
    The loggedEmailTemp is visible outside.
    To use it, write the following lines:
    *
    val loggedProfileViewModel = viewModel<LoggedProfileViewModel>()
    val loggedEmail by loggedProfileViewModel.loggedEmailTemp.collectAsStateWithLifecycle()
    */
    private val _loggedEmailTemp = MutableStateFlow(LoggedProfileState(""))
    val loggedEmailTemp = _loggedEmailTemp.asStateFlow()

    fun setEmailWithoutSaving(email: String) {
        _loggedEmailTemp.value = LoggedProfileState(email)
    }
    // TODO: add way to store email without saving it to shared preferences
}
