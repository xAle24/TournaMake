package com.example.tournaMake.mylibrary.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.tournaMake.mylibrary.displaymodels.BracketDisplayModel
import com.example.tournaMake.mylibrary.internal.Bracket
import com.example.tournaMake.mylibrary.ui.BracketColors
import com.example.tournaMake.mylibrary.ui.BracketDefaults
import kotlinx.coroutines.launch

/**
 * A wrapper around a [Bracket] component for a single elimination bracket. This is different
 * from [MultiEliminationBracket] in that there is only one [bracket] shown, and the tabs associated
 * with it.
 */
@ExperimentalFoundationApi
@Composable
fun SingleEliminationBracket(
    bracket: BracketDisplayModel,
    modifier: Modifier = Modifier,
    colors: BracketColors = BracketDefaults.bracketColors(),
) {
    val pagerState = rememberPagerState { return@rememberPagerState bracket.rounds.size }
    val coroutineScope = rememberCoroutineScope()

    Bracket(
        bracket = bracket,
        selectedRound = bracket.rounds[pagerState.currentPage],
        pagerState = pagerState,
        onSelectedRoundChanged = { round ->
            coroutineScope.launch {
                pagerState.animateScrollToPage(bracket.rounds.indexOf(round))
            }
        },
        modifier = modifier,
        colors = colors,
    )
}
