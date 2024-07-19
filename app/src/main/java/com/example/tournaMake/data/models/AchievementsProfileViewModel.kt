package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tournaMake.sampledata.AchievementResult

class AchievementsProfileViewModel :ViewModel() {
    private val _achievementProfileList = MutableLiveData<List<AchievementResult>>()
    val achievementProfileListLiveData : LiveData<List<AchievementResult>> = _achievementProfileList

    fun updateAchievementProfileList(list: List<AchievementResult>) {
        _achievementProfileList.postValue(list)
    }
}