package com.example.tournaMake.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeState

sealed interface ColorConstants {
    fun getButtonBackground(): Brush
    fun getTextGradient(): Brush
}
data class LightModeColors(
    private val buttonColorStops: List<Color> = listOf(
        Color(195, 167, 15),
        Color(252, 216, 161, 242),
        Color(195, 167, 15)
    ),
    private val textColorStops: List<Color> = listOf(
        Color(59, 11, 119),
        Color(128, 14, 198)
    )
): ColorConstants {
    // Public constants
    private val buttonBackground = Brush.horizontalGradient(colors = buttonColorStops)
    private val tournamakeTextGradient = Brush.horizontalGradient(colors = textColorStops)
    override fun getButtonBackground(): Brush {
        return this.buttonBackground
    }

    override fun getTextGradient(): Brush {
        return this.tournamakeTextGradient
    }
}

data class DarkModeColors(
    private val buttonColorStops: List<Color> = listOf(
        Color(11, 69, 77),
        Color(22, 138, 154, 204),
        Color(11, 69, 77)
    ),
    private val textColorStops: List<Color> = listOf(
        Color(80, 54, 41),
        Color(212, 121, 42)
    )
): ColorConstants {
    private val buttonBackground = Brush.horizontalGradient(colors = buttonColorStops)
    private val tournamakeTextGradient = Brush.verticalGradient(colors = textColorStops)
    override fun getButtonBackground(): Brush {
        return this.buttonBackground
    }

    override fun getTextGradient(): Brush {
        return this.tournamakeTextGradient
    }
}

/**
 * Call this function from inside a Composable
 * that has access to the theme state of the application
 * (all of them should have it).
 * */
@Composable
fun getThemeColors(themeState: ThemeState): ColorConstants {
    return when(themeState.theme) {
        ThemeEnum.Dark -> DarkModeColors()
        ThemeEnum.Light -> LightModeColors()
        ThemeEnum.System -> if (isSystemInDarkTheme()) DarkModeColors() else LightModeColors()
    }
}