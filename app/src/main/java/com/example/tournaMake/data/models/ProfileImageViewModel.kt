package com.example.tournaMake.data.models

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournaMake.data.repositories.ProfileImageRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileImageState(val profileImageUri: Uri)

class ProfileImageViewModel(private val repository: ProfileImageRepository) : ViewModel() {
    val profileImageUri = repository.profileImageUri.map { ProfileImageState(Uri.parse(it)) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ProfileImageState(Uri.EMPTY)
    )
    private val _profileImageUri = MutableLiveData<Uri>(Uri.EMPTY)
    val profileImageLiveData: LiveData<Uri> = _profileImageUri

    fun setAndSaveProfileImageUri(uri: Uri) = viewModelScope.launch {
        repository.setProfileImageUri(uri)
    }
}