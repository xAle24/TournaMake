package com.example.tournaMake.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.example.tournaMake.mylibrary.ui.SingleEliminationBracket
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.data.tournament.TestTournamentData
import com.example.tournaMake.ui.screens.tournament.TournamentScreen
import com.example.tournaMake.ui.screens.tournament.TournamentView
import org.koin.androidx.compose.koinViewModel

class TournamentActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureEdgeToEdgeWindow()

        setContent {
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val state = themeViewModel.state.collectAsStateWithLifecycle()
            TournamentScreen(
                state = state.value
            )
            //SingleEliminationBracket(bracket = TestTournamentData.singleEliminationBracket)
        }
    }

    /**
     * Configures our [MainActivity] window so that it reaches edge to edge of the device, meaning
     * content can be rendered underneath the status and navigation bars.
     *
     * This method works hand in hand with [ConfigureTransparentSystemBars], to make sure content
     * behind these bars is visible.
     *
     * Keep in mind that if you need to make sure your content padding doesn't clash with the status bar text/icons,
     * you can leverage modifiers like `windowInsetsPadding()` and `systemBarsPadding()`. For more information,
     * read the Compose WindowInsets docs: https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/WindowInsets
     */
    private fun configureEdgeToEdgeWindow() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}