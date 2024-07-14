package com.example.tournaMake.ui.screens.match

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tournaMake.R
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.ui.screens.common.BasicScreenWithAppBars
import com.example.tournaMake.ui.theme.getThemeColors

@Composable
fun MatchCreationScreen(
    state: ThemeState,
    backFunction: () -> Unit,
) {
    val imageLogoId =
        if (state.theme == ThemeEnum.Dark) R.drawable.light_writings else R.drawable.dark_writings
    BasicScreenWithAppBars(
        state = state,
        backFunction = backFunction,
        showTopBar = true,
        showBottomBar = false
    ) {
        Column {
            Logo(imageLogoId)
            Spacer(Modifier.height(30.dp))
            RectangleContainer(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f))
            ) {
                SelectionMenu()
                TeamContainer(teamsSet = setOf(testTeam1, testTeam2))
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    BottomTeamScreenButton(
                        state = state,
                        modifier = Modifier
                            .width(150.dp)
                        //.fillMaxWidth(0.3f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    BottomTeamScreenButton(
                        state = state,
                        modifier = Modifier
                            .width(100.dp)
                        //.fillMaxWidth(0.3f)
                    )
                }
            }
        }
    }
}

@Composable
fun BottomTeamScreenButton(
    modifier: Modifier = Modifier,
    state: ThemeState,
    onClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxHeight()
            .clickable { onClick() }
            .clip(RoundedCornerShape(6.dp))
            .background(getThemeColors(themeState = state).getButtonBackground())
    ) {
        Icon(
            Icons.Filled.Add,
            null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .width(25.dp)
        )
        //Spacer(modifier = Modifier.width(10.dp))
        Text(
            "Add Team",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(10.dp)
        )
    }
}

@Composable
fun Logo(
    imageLogoId: Int
) {
    Image(
        painter = painterResource(id = imageLogoId),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .size(100.dp)
        //.background(Color.Black)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionMenu() {
    val gamesNames =
        arrayOf("Tzolk'In", "Call of Duty: Black Ops", "Football", "Mario Kart", "Monster Hunter")
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(gamesNames[0]) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            //.background(Color.Black)
            .padding(32.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            },
            modifier = Modifier.align(Alignment.Center)
        ) {
            TextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.onSecondary,
                    unfocusedContainerColor = Color.White.copy(alpha = 0.7f)
                ),
                label = {
                    Text(
                        text = "Select Game",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
            ) {
                gamesNames.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            selectedText = item
                            expanded = false
                            //Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                            // TODO: add update for the form to complete
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewMatchCreationScreen() {
    MatchCreationScreen(
        state = ThemeState(ThemeEnum.Light),
        backFunction = {}
    )
}