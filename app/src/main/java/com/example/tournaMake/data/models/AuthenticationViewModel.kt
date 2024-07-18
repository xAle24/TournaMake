package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tournaMake.data.repositories.AuthenticationRepository
import com.example.tournaMake.sampledata.MainProfile
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// To keep track of the currently logged in or registered user
// This email refers to a MainProfile
data class LoggedProfileState(val loggedProfileEmail: String)

class AuthenticationViewModel(private val repository: AuthenticationRepository): ViewModel() {
    private val loggedEmail = repository.email.map { LoggedProfileState(it.toString()) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = LoggedProfileState("")
    )
    private val password = repository.password.map { it.toString() }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ""
    )
    /*
    Use loggedEmailTemp if the user does not want the application to remember their
    credentials when closed.*
    The backing property _loggedEmailTemp is mutable, and thus must not be accessed by
    external code.
    The loggedEmailTemp is visible outside.
    To use it, write the following lines:
    *
    val loggedProfileViewModel = viewModel<AuthenticationViewModel>()
    val loggedEmail by loggedProfileViewModel.loggedEmailTemp.collectAsStateWithLifecycle()
    */
    private val _loggedEmailTemp = MutableStateFlow(LoggedProfileState(""))
    val loggedEmailTemp = _loggedEmailTemp.asStateFlow()

    private val _profile = MutableLiveData<MainProfile?>(null)
    val profileLiveData: LiveData<MainProfile?> = _profile
    fun rememberEmailAndPassword(email: String, password: String) = viewModelScope.launch {
        repository.setEmail(email)
        repository.setPassword(password)
    }

    fun setAndSaveEmailTemporarily(email: String) {
        _loggedEmailTemp.value = LoggedProfileState(email)
    }
}