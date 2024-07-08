package com.example.tournaMake.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.ui.screens.registration.RegistrationPhotoScreen
import org.koin.androidx.compose.koinViewModel

class RegistrationPhotoActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val state = themeViewModel.state.collectAsStateWithLifecycle()
            RegistrationPhotoScreen(
                state = state.value,
                loadPhoto = this::loadPhoto,
                back = this::back,
                loadMenu = this::loadMenu
            )
        }
    }

    private fun loadPhoto(){
        //TODO: save photo inside db
    }
    private fun back(){
        finish()
    }
    private fun loadMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }
}