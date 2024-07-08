package com.example.tournaMake.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tournaMake.data.models.ThemeViewModel

class RegistrationPhotoActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel = viewModels<ThemeViewModel>()
            val state = themeViewModel.value.state.collectAsStateWithLifecycle()
            RegistrationPhotoActivity(
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