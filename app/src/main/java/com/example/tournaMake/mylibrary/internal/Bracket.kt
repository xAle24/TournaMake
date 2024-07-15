package com.example.tournaMake.mylibrary.internal

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.tournaMake.mylibrary.displaymodels.BracketDisplayModel
import com.example.tournaMake.mylibrary.displaymodels.BracketRoundDisplayModel
import com.example.tournaMake.mylibrary.ui.BracketColors

/**
 * The main component of a bracket. Given a [bracket], convert the [BracketDisplayModel.rounds]
 * into pages within a horizontal pager, with each page showing the matches for
 * that round. This also includes a tab bar at the top with each round so users can
 * quickly jump to a specific one.
 */
@ExperimentalFoundationApi
@Composable
internal fun Bracket(
    bracket: BracketDisplayModel,
    selectedRound: BracketRoundDisplayModel,
    pagerState: PagerState,
    onSelectedRoundChanged: (BracketRoundDisplayModel) -> Unit,
    modifier: Modifier = Modifier,
    colors: BracketColors,
) {
    Surface(
        modifier = modifier,
        color = Color.Transparent // color behind the match elements
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            BracketRoundTabRow(
                rounds = bracket.rounds,
                selectedRound = selectedRound,
                onRoundSelected = { round ->
                    onSelectedRoundChanged.invoke(round)
                },
                colors = colors.tabColors,
            )

            BracketRoundsPager(
                rounds = bracket.rounds,
                pagerState = pagerState,
                modifier = Modifier
                    .weight(1F),
                colors = colors,
            )
        }
    }
}
