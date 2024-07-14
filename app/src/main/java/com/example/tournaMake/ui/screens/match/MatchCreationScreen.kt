package com.example.tournaMake.ui.screens.match

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tournaMake.R
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.ui.screens.common.BasicScreenWithAppBars

@Composable
fun MatchCreationScreen(
    state: ThemeState,
    backFunction: () -> Unit,
) {
    val imageLogoId =
        if (state.theme == ThemeEnum.Dark) R.drawable.light_writings else R.drawable.dark_writings
    BasicScreenWithAppBars(
        state = state,
        backFunction = backFunction
    ) {
        Column {
            Logo(imageLogoId)
            Spacer(Modifier.height(30.dp))
            FormContainer(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                SelectionMenu()
            }
        }
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
@Composable
fun FormContainer(
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    val backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f) // opacity: 80%
    val screenWidth = LocalConfiguration.current.screenWidthDp
    Column(
        modifier = modifier
            .background(backgroundColor)
            .fillMaxHeight()
            .width((0.9f * screenWidth).dp)
            .verticalScroll(rememberScrollState())
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionMenu() {
    val context = LocalContext.current
    val gamesNames =
        arrayOf("Tzolk'In", "Call of Duty: Black Ops", "Football", "Mario Kart", "Monster Hunter")
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(gamesNames[0]) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
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
                )
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
                            Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
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