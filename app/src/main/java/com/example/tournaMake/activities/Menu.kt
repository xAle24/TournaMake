package com.example.tournaMake.activities

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.Notification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

fun removeNotification(notification: Notification, owner: LifecycleOwner) {
    val db = inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            db.value.notificationDao().removeNotification(notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}