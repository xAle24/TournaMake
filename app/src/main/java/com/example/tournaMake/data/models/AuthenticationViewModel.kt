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
    val loggedEmail = repository.email.map { LoggedProfileState(it.toString()) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = LoggedProfileState("")
    )
    val password = repository.password.map { it.toString() }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ""
    )
    val rememberMe = repository.doesUserWantToBeRemembered.map { it ?: false }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false
    )
    fun saveUserAuthenticationPreferences(email: String, password: String, rememberMe: Boolean) = viewModelScope.launch {
        repository.setEmail(email)
        repository.setPassword(password)
        repository.setRememberMe(rememberMe)
    }
}