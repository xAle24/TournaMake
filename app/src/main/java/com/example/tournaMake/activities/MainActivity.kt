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
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
            // See ThemeViewModel.kt
            val themeViewModel = koinViewModel<ThemeViewModel>()
            // The following line converts the StateFlow contained in the ViewModel
            // to a State object. State objects can trigger recompositions, while
            // StateFlow objects can't. The 'withLifecycle' part ensures this state
            // is destroyed when we leave this Activity.
            val state = themeViewModel.state.collectAsStateWithLifecycle()
            MainScreen(
                state = state.value,
                navigateToRegistration = this::navigateToRegistration,
                navigateToLogin = this::navigateToLogin
            )
            // Test; TODO: remove
            Button(onClick = {
                val intent = Intent(this, TestActivity::class.java)
                startActivity(intent)
            }) {
                Text("Go to test activity")
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToRegistration() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPagePreview() {
    TournaMakeTheme {
        MainActivity().onCreate(null)
    }
}
