package com.example.tournaMake.ui.screens.match

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tournaMake.R
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchCreationScreen(
    state: ThemeState,
    backFunction: () -> Unit,
) {
    val topAppBarBackground =
        if (state.theme == ThemeEnum.Dark) R.drawable.dark_topbarbackground else R.drawable.light_topbarbackground
    val bottomBarBackground =
        if (state.theme == ThemeEnum.Dark) R.drawable.dark_bottom_bar_background else R.drawable.light_bottom_bar_background
    val imageLogoId =
        if (state.theme == ThemeEnum.Dark) R.drawable.light_writings else R.drawable.dark_writings
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Back"
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = backFunction,
                        modifier = Modifier
                            .fillMaxHeight(1f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.dark_tournamake_triangle_no_outline),
                            contentDescription = null,
                            modifier = Modifier
                                .rotate(+90f)
                                .size(50.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            BasicScreenWithTheme(
                state = state
            ) {

            }
        }
    }
}