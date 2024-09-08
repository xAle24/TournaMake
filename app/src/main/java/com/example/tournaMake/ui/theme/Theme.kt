package com.example.tournaMake.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(31, 4, 65),
    secondary = Color(128, 14, 198),
    tertiary = Color(59, 11, 119),
    surface = Color(136, 239, 127),
    surfaceVariant = Color(11, 69, 77),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onSurface = Color.Black,
    outline = Color(33, 197, 220),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(195, 167, 15),
    secondary = Color(251, 200, 21),
    tertiary = Color(252, 219, 179),
    surface = Color(242, 215, 92),
    surfaceVariant = Color(176, 122, 76),
    onPrimary = Color(64, 30, 6),
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onSurface = Color.Black,
    outline = Color(252, 216, 161),
)

@Composable
fun TournaMakeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        /*
        * Note: uncommenting this dynamic color case will cause the DynamicColorSchemes
        * to be used. These are defined by the system, and they try to replicate the overall
        * style of the user's system.
        * */
        /*dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }*/

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}