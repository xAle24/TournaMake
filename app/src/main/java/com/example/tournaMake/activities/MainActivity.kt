package com.example.tournaMake.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.tournaMake.activities.navgraph.NavGraph

class MainActivity : ComponentActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavGraph(
                navController = navController,
                modifier = Modifier,
                owner = this,
                contentResolver = contentResolver,
                window = window
            )
        }
    }
}
