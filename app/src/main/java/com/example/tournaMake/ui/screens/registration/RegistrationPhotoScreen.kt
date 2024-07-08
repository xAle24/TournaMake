package com.example.tournaMake.ui.screens.registration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme

@Composable
fun RegistrationPhotoScreen(
    state: ThemeState,
    loadPhoto:() -> Unit,
    back:() -> Unit,
    loadMenu:() -> Unit
) {
    BasicScreenWithTheme(state = state) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { loadPhoto() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Text(text = "Load Photo")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { back() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Back")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = { loadMenu() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Next")
                }
            }
        }
    }
}
