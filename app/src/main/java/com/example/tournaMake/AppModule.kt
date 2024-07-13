package com.example.tournaMake

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.tournaMake.data.models.GamesListViewModel
import com.example.tournaMake.data.models.LoggedProfileViewModel
import com.example.tournaMake.data.models.GraphViewModel
import com.example.tournaMake.data.models.MatchListViewModel
import com.example.tournaMake.data.models.ProfileListViewModel
import com.example.tournaMake.data.models.ProfileViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.data.repositories.LoggedProfileRepository
import com.example.tournaMake.data.repositories.ThemeRepository
import com.example.tournaMake.sampledata.AppDatabase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore
        by preferencesDataStore("theme")
val appModule = module {
    single { get<Context>().dataStore }
    single { ThemeRepository(get()) }
    viewModel { ThemeViewModel(get()) }
    single { LoggedProfileRepository(get()) }
    viewModel { LoggedProfileViewModel(get()) }
    viewModel { ProfileViewModel() }
    viewModel { ProfileListViewModel() }
    viewModel { MatchListViewModel() }
    viewModel { GraphViewModel() }
    viewModel { GamesListViewModel() }
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "tournamake-database"
        ).build()
    }

}