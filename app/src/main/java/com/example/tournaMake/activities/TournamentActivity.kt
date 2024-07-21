package com.example.tournaMake.activities

//import com.example.tournaMake.mylibrary.ui.SingleEliminationBracket
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.data.models.TournamentDataViewModel
import com.example.tournaMake.data.models.TournamentIDViewModel
import com.example.tournaMake.sampledata.AppDatabase
import com.example.tournaMake.ui.screens.tournament.TournamentScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel

class TournamentActivity : ComponentActivity() {
    private val appDatabase = get<AppDatabase>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureEdgeToEdgeWindow()

        setContent {
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val state = themeViewModel.state.collectAsStateWithLifecycle()
            val tournamentIDViewModel = koinViewModel<TournamentIDViewModel>()
            val tournamentID = tournamentIDViewModel.tournamentID.collectAsStateWithLifecycle()
            val tournamentDataViewModel = koinViewModel<TournamentDataViewModel>()
            fetchStuffForTournament(tournamentID.value, tournamentDataViewModel)

            TournamentScreen(
                state = state.value
            )
            //SingleEliminationBracket(bracket = TestTournamentData.singleEliminationBracket)
        }
    }

    private fun fetchStuffForTournament(
        tournamentID: String,
        tournamentDataViewModel: TournamentDataViewModel
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val tournamentMatchesAndTeamsData = appDatabase.tournamentDao().getMatchesAndTeamsFromTournamentID(tournamentID)
                tournamentDataViewModel.changeMatchesList(tournamentMatchesAndTeamsData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
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