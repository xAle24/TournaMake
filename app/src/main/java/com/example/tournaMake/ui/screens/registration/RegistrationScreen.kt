package com.example.tournaMake.ui.screens.registration

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.tournaMake.R
import com.example.tournaMake.activities.handleRegistration
import com.example.tournaMake.data.models.AuthenticationViewModel
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegistrationScreen(
    navController: NavController,
    owner: LifecycleOwner,
) {
    // See ThemeViewModel.kt
    val themeViewModel = koinViewModel<ThemeViewModel>()
    // The following line converts the StateFlow contained in the ViewModel
    // to a State object. State objects can trigger recompositions, while
    // StateFlow objects can't. The 'withLifecycle' part ensures this state
    // is destroyed when we leave this Activity.
    val state by themeViewModel.state.collectAsStateWithLifecycle()
    val authenticationViewModel = koinViewModel<AuthenticationViewModel>()
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
            var email by remember { mutableStateOf("") }
            var rememberMe by remember { mutableStateOf(false) }
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
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
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
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it }
                )
                Text("Remember me")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                handleRegistration(username, password, email, rememberMe, authenticationViewModel, owner, navController)
                /* SETTING THE "GLOBAL" VARIABLE LOGGED EMAIL! */
                Log.d("DEV", "In RegistrationScreen.kt, email = $email")
            }, modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(60.dp)) {
                Text("Register")
            }
        }
    }
}