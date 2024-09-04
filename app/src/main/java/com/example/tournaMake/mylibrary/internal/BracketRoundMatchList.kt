package com.example.tournaMake.mylibrary.internal

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import com.example.tournaMake.mylibrary.displaymodels.BracketMatchDisplayModel
import com.example.tournaMake.mylibrary.ui.BracketColors

@Composable
internal fun BracketRoundMatchList(
    matches: List<BracketMatchDisplayModel>,
    itemHeight: Dp,
    lazyListState: LazyListState,
    colors: BracketColors,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight(),
        state = lazyListState,
    ) {
        items(matches) { match ->
            BracketMatchItem(
                match = match,
                modifier = Modifier
                    .height(itemHeight),
                colors = colors.matchColors,
                navController = navController
            )
        }
    }
}
