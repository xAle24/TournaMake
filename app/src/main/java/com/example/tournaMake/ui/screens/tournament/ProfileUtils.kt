package com.example.tournaMake.ui.screens.tournament

import com.example.tournaMake.sampledata.GuestProfile
import com.example.tournaMake.sampledata.MainProfile

data class FilteredProfiles(
    val mainProfiles: List<MainProfile>,
    val guestProfile: List<GuestProfile>
)
class ProfileUtils(private val mainProfileList : List<MainProfile>,
                   private val guestProfileList: List<GuestProfile>) {
    fun filteredProfiles(username: String) : FilteredProfiles {
        val mainProfiles = mainProfileList.filter { it.username >= username }
        val guestProfiles = guestProfileList.filter { it.username >= username }
        return FilteredProfiles(mainProfiles, guestProfiles)
    }
}