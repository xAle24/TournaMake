package com.example.tournaMake.ui.screens.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun RectangleContainer(
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width((0.9f * screenWidth).dp)
        //.verticalScroll(rememberScrollState())
    ) {
        content()
    }
}