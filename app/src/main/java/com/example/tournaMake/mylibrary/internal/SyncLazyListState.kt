package com.example.tournaMake.mylibrary.internal

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

/**
 * Syncs a [LazyListState] with some [otherState] by observing this one's scroll offset
 * and applying those same values to the [otherState].
 */
@Composable
internal fun LazyListState.SyncLazyListState(otherState: LazyListState) {
    LaunchedEffect(this.firstVisibleItemScrollOffset) {
        if (!otherState.isScrollInProgress) {
            otherState.scrollToItem(
                this@SyncLazyListState.firstVisibleItemIndex,
                this@SyncLazyListState.firstVisibleItemScrollOffset,
            )
        }
    }

    LaunchedEffect(otherState.firstVisibleItemScrollOffset) {
        if (!this@SyncLazyListState.isScrollInProgress) {
            this@SyncLazyListState.scrollToItem(
                otherState.firstVisibleItemIndex,
                otherState.firstVisibleItemScrollOffset,
            )
        }
    }
}
