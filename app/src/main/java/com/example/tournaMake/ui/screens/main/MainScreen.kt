package com.example.tournaMake.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tournaMake.R
import com.example.tournaMake.data.models.ThemeEnum.*
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme

@Composable
fun MainScreen(
    state: ThemeState,
    navigateToRegistration: () -> Unit,
    navigateToLogin: () -> Unit
) {
    BasicScreenWithTheme(
        state = state,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Add an image that changes based on the theme state
            val imageId = if (state.theme == Dark) R.drawable.light_writings else R.drawable.dark_writings
            Image(
                painter = painterResource(id = imageId),
                contentDescription = "Appropriate theme image",
                modifier = Modifier.fillMaxWidth(0.8f).fillMaxHeight(0.2f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = navigateToLogin, modifier = Modifier.fillMaxWidth(0.8f).height(60.dp)) {
                Text("Login", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = navigateToRegistration, modifier = Modifier.fillMaxWidth(0.8f).height(60.dp)) {
                Text("Register", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
        }
    }
}