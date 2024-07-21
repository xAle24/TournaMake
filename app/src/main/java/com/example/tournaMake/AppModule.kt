package com.example.tournaMake

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.tournaMake.data.models.AchievementsProfileViewModel
import com.example.tournaMake.data.models.GamesListViewModel
import com.example.tournaMake.data.models.GraphViewModel
import com.example.tournaMake.data.models.AuthenticationViewModel
import com.example.tournaMake.data.models.MatchListViewModel
import com.example.tournaMake.data.models.ProfileListViewModel
import com.example.tournaMake.data.models.ProfileViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.data.models.TournamentCreationViewModel
import com.example.tournaMake.data.models.TournamentIDViewModel
import com.example.tournaMake.data.models.TournamentListViewModel
import com.example.tournaMake.data.repositories.AuthenticationRepository
import com.example.tournaMake.data.repositories.ThemeRepository
import com.example.tournaMake.data.repositories.TournamentIDRepository
import com.example.tournaMake.sampledata.AppDatabase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore
        by preferencesDataStore("theme")
val appModule = module {
    single { get<Context>().dataStore }
    single { ThemeRepository(get()) } // basically the call to get() means "get your parameters from the stuff koin has saved here"
    viewModel { ThemeViewModel(get()) }
    single { AuthenticationRepository(get()) }
    viewModel { AuthenticationViewModel(get()) }
    viewModel { ProfileViewModel() }
    viewModel { ProfileListViewModel() }
    viewModel { MatchListViewModel() }
    viewModel { GraphViewModel() }
    viewModel { GamesListViewModel() }
    viewModel { TournamentListViewModel() }
    viewModel { AchievementsProfileViewModel() }
    viewModel { TournamentCreationViewModel() }
    single { TournamentIDRepository(get()) }
    viewModel { TournamentIDViewModel(get()) }
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "tournamake-database"
        ).build()
    }

}