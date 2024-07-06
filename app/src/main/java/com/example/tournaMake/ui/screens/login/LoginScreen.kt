package com.example.tournaMake.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme

@Composable
fun LoginScreen(
    state: ThemeState, // The state of the UI (dark, light or system)
    navigateToMenu: () -> Unit
) {
    BasicScreenWithTheme(
        state = state,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var username by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            OutlinedTextField(
                value = username,
                modifier = Modifier.fillMaxWidth(0.8f).height(60.dp),
                onValueChange = { username = it },
                label = { Text("Username") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                modifier = Modifier.fillMaxWidth(0.8f).height(60.dp),
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { navigateToMenu() }, modifier = Modifier.fillMaxWidth(0.8f).height(60.dp)) {
                Text("Login")
            }
        }
    }
}