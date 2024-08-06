package com.example.tournaMake.ui.screens.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tournaMake.R
import com.example.tournaMake.data.models.AuthenticationViewModel
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme

@Composable
fun LoginScreen(
    state: ThemeState, // The state of the UI (dark, light or system)
    viewModel: AuthenticationViewModel,
    handleLogin: (String, String, Boolean) -> Unit
) {
    BasicScreenWithTheme(
        state = state,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Log.d("DEV-LOGIN", "Did user want to be remembered? ${viewModel.didUserWantToBeRemembered()}")
            var rememberMe by remember { mutableStateOf(viewModel.didUserWantToBeRemembered()) }
            var email by remember {
                mutableStateOf(
                    if (viewModel.didUserWantToBeRemembered()) viewModel.getRememberedEmail().loggedProfileEmail else ""
                )
            }
            var password by remember {
                mutableStateOf(
                    if (viewModel.didUserWantToBeRemembered()) viewModel.getRememberedPassword() else ""
                )
            }
            Log.d("DEV-LOGIN", "Value of email: $email, value of pw: $password")
            val imageId =
                if (state.theme == ThemeEnum.Dark) R.drawable.light_writings else R.drawable.dark_writings
            Image(
                painter = painterResource(id = imageId),
                contentDescription = "Appropriate theme image",
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.2f)
            )
            OutlinedTextField(
                value = email,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp),
                onValueChange = { email = it },
                label = { Text("Email") },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp),
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = {
                        rememberMe = it
                        viewModel.setRememberMe(it)
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.secondary
                    )
                )
                Text("Remember me")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    handleLogin(email, password, rememberMe)
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp)
            ) {
                Text("Login")
            }
        }
    }
}