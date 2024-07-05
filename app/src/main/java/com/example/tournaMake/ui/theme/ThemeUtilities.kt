package com.example.tournaMake.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.example.tournaMake.R

/**
 * Documentation on CompositionLocal:
 * https://developer.android.com/reference/kotlin/androidx/compose/runtime/CompositionLocal
 * It basically represents a resource accessible to many Components.
 * Needs a CompositionLocalProvider to be passed to appropriate Components.
 * */
val LocalBackgroundImageId = compositionLocalOf { 0 }

@Composable
fun getBackgroundImageId(darkMode: Boolean): Int {
    return if (darkMode) {
        R.drawable.dark_background
    } else {
        R.drawable.light_background
    }
}