package com.example.tournaMake.mylibrary.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.tournaMake.mylibrary.displaymodels.BracketDisplayModel
import com.example.tournaMake.mylibrary.internal.Bracket
import kotlinx.coroutines.launch

/**
 * An extension on [Bracket] that is meant to support multiple elimination rounds, such a double elimination
 * tournament. We do this by taking in multiple [brackets], and setting up an [ExposedDropdownMenuBox] to switch
 * between each bracket.
 */
@ExperimentalMaterial3Api
@ExperimentalFoundationApi
@Composable
fun MultiEliminationBracket(
    brackets: List<BracketDisplayModel>,
    modifier: Modifier = Modifier,
    colors: BracketColors = BracketDefaults.bracketColors(),
    navController: NavController
) {
    var dropdownExpanded by remember {
        mutableStateOf(false)
    }

    var selectedBracket by remember {
        mutableStateOf(brackets.first())
    }
    //val pageIndex = remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(pageCount = { return@rememberPagerState selectedBracket.rounds.size })
    val coroutineScope = rememberCoroutineScope()

    Column {
        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = {
                dropdownExpanded = !dropdownExpanded
            },
        ) {
            TextField(
                value = selectedBracket.name,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = colors.dropdownColors.textFieldColors,
            )

            ExposedDropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = {
                    dropdownExpanded = false
                },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.tertiary) // for the background of the dropdown menu
            ) {
                brackets.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item.name) },
                        onClick = {
                            selectedBracket = item
                            coroutineScope.launch {
                                pagerState.scrollToPage(0)
                            }
                            dropdownExpanded = false
                        },
                        colors = colors.dropdownColors.menuItemColors,
                    )
                }
            }
        }

        Bracket(
            bracket = selectedBracket,
            selectedRound = selectedBracket.rounds[pagerState.currentPage],
            pagerState = pagerState,
            modifier = modifier,
            onSelectedRoundChanged = { round ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(selectedBracket.rounds.indexOf(round))
                }
            },
            colors = colors,
            navController = navController
        )
    }
}
