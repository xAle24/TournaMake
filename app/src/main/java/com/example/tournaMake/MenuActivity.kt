package com.example.tournaMake

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tournaMake.ui.theme.TournaMakeTheme

class MenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TournaMakeTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.background),
                            modifier = Modifier.fillMaxSize(),
                            contentDescription = "Background Image",
                            contentScale = ContentScale.Crop
                        )
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(onClick = {  }) {
                                Text("Start")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = {  }) {
                                Text("Profile")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { navigateToSetting() }) {
                                Text("Setting")
                            }
                        }
                    }
                }
            }
        }
    }
    private fun navigateToSetting() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}