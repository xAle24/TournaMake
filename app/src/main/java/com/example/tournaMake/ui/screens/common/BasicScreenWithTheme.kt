package com.example.tournaMake.ui.screens.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.ui.theme.LocalBackgroundImageId
import com.example.tournaMake.ui.theme.TournaMakeTheme
import com.example.tournaMake.ui.theme.getBackgroundImageId

/**
 * All Screens that need a background image and the state of the theme
 * should use this Composable as component container.
 *
 * For example, the ExampleScreen should have some code looking like this:
 * fun ExampleScreen(
 *  state: ThemeState,
 *  isSystemInDarkModeCustom: () -> Boolean,
 *  // other optional params if needed
 * ) {
 *  BasicScreenWithTheme(
 *      state = state,
 *      isSystemInDarkModeCustom = isSystemInDarkModeCustom
 *  ) {
 *      // The rest of your content here
 *  }
 * }
 * */
@Composable
fun BasicScreenWithTheme(
    state: ThemeState, // The state of the UI (dark, light or system)
    isSystemInDarkModeCustom: () -> Boolean, // see SettingsActivity.kt
    content: @Composable () -> Unit
) {
    TournaMakeTheme(
        darkTheme = when (state.theme) {
            ThemeEnum.Light -> false
            ThemeEnum.Dark -> true
            ThemeEnum.System -> isSystemInDarkTheme()
        }
    ) {
        val backgroundImageId = getBackgroundImageId( // defined in ThemeUtilities.kt
            darkMode = state.theme == ThemeEnum.Dark || (state.theme == ThemeEnum.System && isSystemInDarkModeCustom())
        )
        /* CompositionLocalProvider is a Composable (so it has the usual structure
        * Composable (params) { content }
        * ), which is used every time we want the background image according to the
        * current theme of the app. */
        CompositionLocalProvider(LocalBackgroundImageId provides backgroundImageId) {
            // Encapsulating the buttons in a Box, that fills the whole background
            // and can therefore be coloured as wanted.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .wrapContentSize(Alignment.TopCenter)
            ) {
                Image( // the background Image
                    painter = painterResource(id = backgroundImageId), // provided by the CompositionLocalProvider
                    contentDescription = "Background Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                content() // the rest of the content
            }
        }
    }
}