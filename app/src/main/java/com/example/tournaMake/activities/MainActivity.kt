package com.example.tournaMake.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.tournaMake.activities.navgraph.NavGraph
import com.example.tournaMake.activities.test.TestActivity
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.ui.screens.main.MainScreen
import com.example.tournaMake.ui.theme.TournaMakeTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navGraph = rememberNavController()
            NavGraph(
                navController = navGraph,
                modifier = Modifier,
                owner = this,
                contentResolver = contentResolver,
                window = window
            )
        }
    }
}
