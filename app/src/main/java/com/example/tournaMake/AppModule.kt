package com.example.tournaMake

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.data.repositories.ThemeRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore
        by preferencesDataStore("theme")
val appModule = module {
    single { get<Context>().dataStore }
    single { ThemeRepository(get()) }
    viewModel { ThemeViewModel(get()) }
}