package com.example.tournaMake.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.tournaMake.activities.navgraph.NavigationRoute
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.ui.screens.common.BasicScreenWithAppBars
import com.example.tournaMake.ui.theme.getThemeColors
import org.koin.androidx.compose.koinViewModel

/**
 * Builds the Settings Screen. Called in SettingsActivity.
 * */
@Composable
fun SettingsScreen(
    navController: NavController,
) {
    // See ThemeViewModel.kt
    val themeViewModel = koinViewModel<ThemeViewModel>()
    // The following line converts the StateFlow contained in the ViewModel
    // to a State object. State objects can trigger recompositions, while
    // StateFlow objects can't. The 'withLifecycle' part ensures this state
    // is destroyed when we leave this Activity.
    val state by themeViewModel.state.collectAsStateWithLifecycle()
    val colorConstants = getThemeColors(themeState = state)
    val isSystemInDarkTheme = isSystemInDarkTheme()
    BasicScreenWithAppBars(
        state = state,
        backFunction = { navController.navigate(NavigationRoute.MenuScreen.route) },
        showTopBar = true,
        showBottomBar = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            Button(
                onClick = {
                    val newTheme = when (state.theme) {
                        ThemeEnum.Light -> ThemeEnum.Dark
                        ThemeEnum.Dark -> ThemeEnum.Light
                        ThemeEnum.System -> if (isSystemInDarkTheme) ThemeEnum.Light else ThemeEnum.Dark
                    }
                    themeViewModel.changeTheme(newTheme) // callback passed as parameter to this function
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(30.dp))
                    .height(60.dp)
                    .background(colorConstants.getButtonBackground()),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                border = BorderStroke(3.dp, MaterialTheme.colorScheme.outline)
            ) {
                // Button Content
                Text(text = "Toggle Theme")
            }
        }
    }
}