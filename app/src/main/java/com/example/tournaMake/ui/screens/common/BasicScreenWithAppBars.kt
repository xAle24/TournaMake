package com.example.tournaMake.ui.screens.common

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tournaMake.R
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeState

private val barHeight: Dp = 60.dp
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedBoxWithConstraintsScope")
@Composable
fun BasicScreenWithAppBars(
    state: ThemeState,
    backFunction: () -> Unit,
    content: @Composable () -> Unit
) {
    val backButtonIcon =
        if (state.theme == ThemeEnum.Dark) R.drawable.dark_tournamake_triangle_no_outline else R.drawable.light_tournamake_triangle_no_outline
    val topAppBarBackground =
        if (state.theme == ThemeEnum.Dark) R.drawable.dark_topbarbackground else R.drawable.light_topbarbackground
    val bottomBarBackground =
        if (state.theme == ThemeEnum.Dark) R.drawable.dark_bottom_bar_background else R.drawable.light_bottom_bar_background
    BasicScreenWithTheme(state = state) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // The top app bar with the custom background image
            TournaMakeTopAppBar(
                backButtonIcon = backButtonIcon,
                topAppBarBackground = topAppBarBackground,
                backFunction = backFunction
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    //.background(Color.White)
                    .padding(top = barHeight + 5.dp, bottom = barHeight + 5.dp) // Adjust top padding to match top app bar height
            ) {
                // The custom content, passed as a parameter
                content()
                //Text("Prova bottom", modifier = Modifier.align(Alignment.BottomStart))
            }

            // The bottom app bar with the custom background image
            TournaMakeBottomAppBar(
                backgroundImage = bottomBarBackground,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

/**
 * Creates a Top App Bar, with the "Back" text
 * centered horizontally within the image background.
 * It also provides a
 * */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun TournaMakeTopAppBar(
    backButtonIcon: Int,
    topAppBarBackground: Int,
    backFunction: () -> Unit
) {
    Box(
        /*
        * This box limits the top app bar to a small section of the
        * screen, and binds it to the top left of the screen.
        * It is the main container of the TopAppBar.
        * */
        modifier = Modifier
            .height(barHeight)
            .fillMaxWidth(),
        contentAlignment = Alignment.TopStart
    ) {
        /*
        * Box with constraints allow elements to overlap
        * */
        BoxWithConstraints {
            Image(
                modifier = Modifier
                    .fillMaxSize(),
                painter = painterResource(id = topAppBarBackground),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                IconButton( // The back button
                    onClick = backFunction,
                    modifier = Modifier
                        .size(70.dp)
                        //.background(Color.Black)
                        .padding(bottom = 10.dp),
                ) {
                    Image(
                        painter = painterResource(id = backButtonIcon),
                        contentDescription = "Back",
                        modifier = Modifier
                            .rotate(+90f)
                            .size(50.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    "Back",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(bottom = 10.dp),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}

@Composable
fun TournaMakeBottomAppBar(
    backgroundImage: Int,
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .height(barHeight)
            .fillMaxWidth(),
        contentAlignment = Alignment.BottomStart
    ) {
        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}

@Preview
@Composable
fun PreviewBasicScreenWithScaffold() {
    BasicScreenWithAppBars(state = ThemeState(ThemeEnum.Light), backFunction = { /*TODO*/ }) {
        Text("Ciao mamma")
    }
}


