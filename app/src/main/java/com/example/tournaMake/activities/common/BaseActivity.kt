package com.example.tournaMake.activities.common

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.data.models.ThemeViewModel
import kotlinx.coroutines.flow.StateFlow

// I wasn't able to make this work

/*
abstract class BaseActivity : ComponentActivity() {
    protected val themeViewModel = viewModels<ThemeViewModel>()
    protected var state = StateFlow<ThemeEnum> by lazy {
        themeViewModel.state.collectAsStateWithLifecycle(initialValue = ThemeEnum.System)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Content()
        }
    }

    @Composable
    protected fun ProvideUIState() {
        this.state = themeViewModel.value.state.collectAsStateWithLifecycle()
    }

    @Composable
    abstract fun Content()
}*/
