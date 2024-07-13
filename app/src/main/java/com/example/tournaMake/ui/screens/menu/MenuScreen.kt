package com.example.tournaMake.ui.screens.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tournaMake.R
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme
import com.example.tournaMake.ui.theme.getThemeColors

@Composable
fun MenuScreen(
    state: ThemeState,
    navigateToSettings: () -> Unit,
    navigateToListProfile: () -> Unit,
    navigateToTournament: () -> Unit,
    navigateToGamesList: () -> Unit,
    navigateToMatchesList: () -> Unit
) {
    BasicScreenWithTheme(
        state = state,
    ) {
        val imageLogoId =
            if (state.theme == ThemeEnum.Dark) R.drawable.light_writings else R.drawable.dark_writings
        val imageKnightId =
            if (state.theme == ThemeEnum.Dark) R.drawable.light_knight else R.drawable.dark_knight
        val topAppBarBackground =
            if (state.theme == ThemeEnum.Dark) R.drawable.dark_topbarbackground else R.drawable.light_topbarbackground
        val bottomBarBackground =
            if (state.theme == ThemeEnum.Dark) R.drawable.dark_bottombarbackground else R.drawable.light_bottombarbackground
        val showDialog = remember { mutableStateOf(false) }
        if (showDialog.value) {
            ShowNotification(
                openDialog = showDialog,
                onDismiss = { showDialog.value = false },
                listItems = listOf("Notification 1", "Notification 1", "Notification 3")
            )
        }
        Column(
            modifier = Modifier.fillMaxHeight(1f),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .weight(0.1f) // 10% of the screen height
            ) {
                Image(
                    painter = painterResource(id = topAppBarBackground),
                    contentDescription = "top app bar background",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .height(70.dp)
                        .fillMaxWidth(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MenuButton(
                    "Logout", { /* Handle left button click */ }, R.drawable.backicon, state,
                    modifier = Modifier
                        .fillMaxWidth(0.2f)
                        .fillMaxHeight(0.1f)
                )
                MenuButton("", { showDialog.value = true }, R.drawable.bellnotification, state,
                    modifier = Modifier
                        .fillMaxWidth(0.1f)
                        .fillMaxHeight(0.1f)
                )
            }
            Image(
                painter = painterResource(id = imageLogoId),
                contentDescription = "Appropriate logo image",
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.2f)
            )
            MenuButton("Tournament", navigateToTournament, R.drawable.triangleicon, state)
            MenuButton("Matches", navigateToMatchesList, R.drawable.d20_dark, state)
            MenuButton("Game list", navigateToGamesList, R.drawable.listicon, state)
            MenuButton("Profile", navigateToListProfile, R.drawable.profileicon, state)
            MenuButton("Settings", navigateToSettings, R.drawable.settingsicon, state)

            Image(
                painter = painterResource(id = imageKnightId),
                contentDescription = "Appropriate theme image",
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.5f)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .weight(0.1f) // 10% of the screen height
            ) {
                Image(
                    painter = painterResource(id = bottomBarBackground),
                    contentDescription = "top app bar background",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .height(70.dp)
                        .fillMaxWidth(1f)
                )
            }
        }
    }
}

@Composable
fun MenuButton(
    text: String, onClick: () -> Unit, iconId: Int, state: ThemeState, modifier: Modifier = Modifier
        .fillMaxWidth(0.8f)
        .height(60.dp)
) {
    val colorConstants = getThemeColors(themeState = state)
    Button(
        onClick = onClick,
        modifier = modifier.background(colorConstants.getButtonBackground()),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        )
    ) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = null,
            modifier = Modifier
                .width(40.dp)
                .height(40.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(text)
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun ShowNotification(
    openDialog: MutableState<Boolean>,
    onDismiss: () -> Unit,
    listItems: List<String>
) {
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            title = {
                Text(text = "Notification:")
            },
            text = {
                LazyColumn {
                    items(listItems) { item ->
                        Text(text = item, modifier = Modifier.padding(8.dp))
                    }
                }
            },
            confirmButton = {
                Button(onClick = { onDismiss() }) {
                    Text("Close")
                }
            }
        )
    }
}