package com.example.tournaMake.data.repositories

import com.example.tournaMake.sampledata.AppDatabase

class NotificationsRepository(private val appDatabase: AppDatabase) {
    fun getNotificationsLiveData(email: String)
    = appDatabase.notificationDao().getNotificationsByEmail(email)
}