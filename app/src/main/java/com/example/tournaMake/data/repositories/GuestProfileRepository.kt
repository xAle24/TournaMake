package com.example.tournaMake.data.repositories

import com.example.tournaMake.sampledata.AppDatabase

class GuestProfileRepository(private val appDatabase: AppDatabase) {
    fun getAllGuestProfile() = appDatabase.guestProfileDao().getAll()
}