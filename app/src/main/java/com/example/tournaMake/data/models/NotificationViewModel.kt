package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tournaMake.sampledata.Notification

class NotificationViewModel : ViewModel() {
    private val _notificationList = MutableLiveData<List<Notification>>()
    val notificationLiveData : LiveData<List<Notification>> = _notificationList

    fun changeNotificationList(list: List<Notification>) {
        _notificationList.postValue(list)
    }
}