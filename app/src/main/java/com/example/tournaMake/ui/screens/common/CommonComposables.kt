package com.example.tournaMake.ui.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout

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

@Composable
fun CustomBorderCard() {

    Box(
        modifier = Modifier
            .padding(8.dp)
            .width(150.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(10.dp)) // Rounded top corners
            .border(
                width = 2.dp, // Thinner border for all sides
                color = Color(0xFF00796B), // Border color
                shape = RoundedCornerShape(10.dp)
            )
    ) {

        // Inner content, e.g., text
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF00ACC1)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Settings",
                color = Color.White
            )
        }
        // Add a Box to create the thicker top-left border

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 6.dp))
                .height(12.dp)
                .width(100.dp)
                .background(Color(0xFF00796B))
        )
    }
}

@Composable
fun ButtonThickBorder(
    modifier: Modifier = Modifier,
    radius: Int = 6,
    thickness: Int = 12,
    width: Int = 100,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = radius.dp))
            .height(thickness.dp)
            .width(width.dp)
            .background(MaterialTheme.colorScheme.outline)
    )
}

@Composable
fun ButtonThickBorderContainer(
    content: @Composable () -> Unit,
    radius: Int = 6,
    thickness: Int = 12,
    width: Int = 100,
    offsetX: Int = 11,
    offsetY: Int = 1,
    withThickBorder: Boolean = true
) {
    ConstraintLayout {
        val (box) = createRefs()
        Box {
            content()
        }
        if (withThickBorder) {
            ButtonThickBorder(
                modifier = Modifier
                    .constrainAs(box) {
                        top.linkTo(parent.top, margin = offsetY.dp)
                        start.linkTo(parent.start, margin = offsetX.dp)
                    },
                radius = radius,
                thickness = thickness,
                width = width,
            )
        }
    }
}

@Preview
@Composable
fun MyPreview() {
    CustomBorderCard()
}

@Preview
@Composable
fun ExampleBox() {
    ButtonThickBorder()
}