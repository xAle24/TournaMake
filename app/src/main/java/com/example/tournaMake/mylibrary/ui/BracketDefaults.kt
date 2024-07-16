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
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.Black,
            ),
            matchColors = BracketColors.MatchColors(
                contentColor = MaterialTheme.colorScheme.onTertiary, // writings in the text fields of the matches
                borderColor = MaterialTheme.colorScheme.primary,
                dividerColor = MaterialTheme.colorScheme.primary,
            ),
            dropdownColors = BracketColors.DropdownColors(
                textFieldColors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
                    focusedContainerColor = MaterialTheme.colorScheme.secondary
                ),
                menuItemColors = MenuDefaults.itemColors(),
            ),
        )
    }
}
