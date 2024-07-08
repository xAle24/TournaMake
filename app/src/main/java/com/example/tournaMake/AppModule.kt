package com.example.tournaMake

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.tournaMake.data.models.ThemeViewModel
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
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "tournamake-database"
        ).build()
    }

}