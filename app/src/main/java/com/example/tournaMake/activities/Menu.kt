package com.example.tournaMake.activities

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.NotificationViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.sampledata.Notification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

fun fetchAndUpdateNotification(
    notificationViewModel: NotificationViewModel,
    email: String,
    owner: LifecycleOwner
) {
    val appDatabase = inject<AppDatabase>(AppDatabase::class.java)
    var notificationList: List<Notification>
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            notificationList = appDatabase.value.notificationDao().getNotificationsByEmail(email)
            notificationViewModel.changeNotificationList(notificationList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
