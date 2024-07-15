package com.example.tournaMake.ui.screens.tournament

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tournaMake.mylibrary.displaymodels.BracketDisplayModel
import com.example.tournaMake.mylibrary.ui.MultiEliminationBracket
import com.example.tournaMake.mylibrary.ui.SingleEliminationBracket
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.data.tournament.TestTournamentData
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun TournamentScreen(
    state: ThemeState
) {
    BasicScreenWithTheme(
        state = state
    ) {
        //TODO implement graph
        TournamentView()
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TournamentView() {
    ConfigureTransparentSystemBars()

    Surface(
        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f), // the background behind the whole bracket system
        modifier = Modifier
            .systemBarsPadding(),
    ) {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(
                        onClick = {
                            navController.navigate("single_elimination")
                        },
                    ) {
                        Text(
                            text = "Single Elimination",
                        )
                    }

                    Button(
                        onClick = {
                            navController.navigate("double_elimination")
                        },
                    ) {
                        Text(
                            text = "Double Elimination",
                        )
                    }
                }
            }
            composable("single_elimination") {
                SingleEliminationBracket(bracket = TestTournamentData.singleEliminationBracket)
            }
            composable("double_elimination") {
                val brackets = listOf(
                    BracketDisplayModel("Upper Bracket", TestTournamentData.upperBracketRounds),
                    BracketDisplayModel("Lower Bracket", TestTournamentData.lowerBracketRounds),
                )

                MultiEliminationBracket(brackets = brackets)
            }
        }
    }
}

@Composable
private fun ConfigureTransparentSystemBars() {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()

    DisposableEffect(systemUiController, useDarkIcons) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons,
        )

        onDispose { }
    }
}