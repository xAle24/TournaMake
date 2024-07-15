package com.example.tournaMake.mylibrary.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.tournaMake.mylibrary.ui.BracketColors

/**
 * Provides various helper functions for default behaviors of bracket configurations.
 */
object BracketDefaults {

    /**
     * Default value of [BracketColors] class.
     */
    @Composable
    fun bracketColors(): BracketColors {
        return BracketColors(
            tabColors = BracketColors.TabColors(
                containerColor = Color.Transparent,
                contentColor = Color.Black,
            ),
            matchColors = BracketColors.MatchColors(
                contentColor = MaterialTheme.colorScheme.onSurface,
                borderColor = MaterialTheme.colorScheme.outline,
                dividerColor = MaterialTheme.colorScheme.outlineVariant,
            ),
            dropdownColors = BracketColors.DropdownColors(
                textFieldColors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                ),
                menuItemColors = MenuDefaults.itemColors(
                    textColor = Color.Transparent,

                ),
            ),
        )
    }
}
