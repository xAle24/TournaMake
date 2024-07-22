package com.example.tournaMake.data.models

import androidx.lifecycle.viewModelScope
import com.example.tournaMake.data.repositories.AuthenticationRepository
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.lang.IllegalStateException

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

    fun setRememberMe(rememberMe: Boolean) = viewModelScope.launch {
        repository.setRememberMe(rememberMe)
    }

    fun didUserWantToBeRemembered(): Boolean {
        return this.rememberMe.value
    }

    fun getRememberedEmail(): StateFlow<LoggedProfileState> {
        if (this.rememberMe.value) {
            return this.loggedEmail
        } else {
            throw IllegalStateException("The user did not select 'Remember me' option!")
        }
    }

    fun getRememberedPassword(): StateFlow<String> {
        if (this.rememberMe.value) {
            return this.password
        } else {
            throw IllegalStateException("The user did not select 'Remember me' option!")
        }
    }
}