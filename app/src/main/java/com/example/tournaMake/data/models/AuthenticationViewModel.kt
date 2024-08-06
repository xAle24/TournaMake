package com.example.tournaMake.data.models

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.tournaMake.data.repositories.AuthenticationRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.sampledata.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import org.koin.core.context.GlobalContext.get
import java.lang.IllegalStateException

// To keep track of the currently logged in or registered user
// This email refers to a MainProfile
data class LoggedProfileState(val loggedProfileEmail: String)

enum class LoginStatus {Success, Fail, Unknown}


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
    private val _loginStatus = MutableStateFlow(LoginStatus.Unknown)
    val loginStatus: StateFlow<LoginStatus> = _loginStatus.asStateFlow()
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

    fun getRememberedEmail(): LoggedProfileState {
        if (this.rememberMe.value) {
            Log.d("DEV-LOGIN-VM", "Value of remembered email: ${this.loggedEmail.value.loggedProfileEmail}")
            return this.loggedEmail.value
        } else {
            throw IllegalStateException("The user did not select 'Remember me' option!")
        }
    }

    fun getRememberedPassword(): String {
        if (this.rememberMe.value) {
            Log.d("DEV-LOGIN-VM", "Value of remembered password: ${this.password.value}")
            return this.password.value
        } else {
            throw IllegalStateException("The user did not select 'Remember me' option!")
        }
    }

    fun changeLoginStatus(status: LoginStatus) {
        this._loginStatus.value = status
        Log.d("DEV-LOGIN-VM", "ViewModel: value of login status = ${loginStatus.value}")
    }
}