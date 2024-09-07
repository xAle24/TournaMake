package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.tournaMake.data.repositories.NotificationsRepository
import com.example.tournaMake.sampledata.Notification

class NotificationViewModel(repository: NotificationsRepository) : ViewModel() {
    private val _loggedEmail = MutableLiveData<String>()
    private val loggedEmail: LiveData<String> = _loggedEmail

    /**
     * SwitchMap is an extension method of live data which is used when a conversion
     * from one type of live data to another one is needed.
     * In this case, we are observing the currently logged email live data [_loggedEmail].
     * When the email is set by the [changeLoggedEmail] method, we want to start
     * observing all the user's notifications in the db.
     * We can achieve this by mapping the email live data to the notifications live data,
     * passing its current value for email as a parameter to the method that
     * gets the live data from the Room db.
     * Long story short, if you need to observe live data from the database and have to pass
     * some parameters like an ID, use switchMap.
     * More details:
     * https://medium.com/@kalyanraghu/an-example-of-livedata-switchmap-transformation-function-ceddb1a44c58
     * https://stackoverflow.com/questions/75465435/unresolved-reference-transformations-after-upgrading-lifecycle-dependency
     * https://medium.com/@gsaillen95/how-to-use-transformations-in-android-with-mvvm-cfa4832774bc#:~:text=Basically%20with%20the%20map%20function,the%20data%20that%20is%20changing
     * */
    val notificationLiveData : LiveData<List<Notification>> = loggedEmail.switchMap { email ->
        repository.getNotificationsLiveData(email)
    }

    fun changeLoggedEmail(email: String) {
        _loggedEmail.postValue(email)
    }
}