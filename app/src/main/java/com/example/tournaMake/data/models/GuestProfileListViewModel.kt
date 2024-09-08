package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.tournaMake.data.repositories.GuestProfileRepository
import com.example.tournaMake.sampledata.GuestProfile

class GuestProfileListViewModel(repository: GuestProfileRepository) : ViewModel() {
    val guestProfileListLiveData : LiveData<List<GuestProfile>> = repository.getAllGuestProfile()
}