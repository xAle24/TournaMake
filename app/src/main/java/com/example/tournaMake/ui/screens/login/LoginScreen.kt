package com.example.tournaMake.ui.screens.login

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.tournaMake.R
import com.example.tournaMake.activities.handleLogin
import com.example.tournaMake.data.models.AuthenticationViewModel
import com.example.tournaMake.data.models.BlockingCredentialsFetcher
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme
import com.example.tournaMake.ui.theme.getThemeColors
import kotlinx.coroutines.runBlocking
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun LoginScreen(
    navController: NavController,
    owner: LifecycleOwner,
) {
    val context = LocalContext.current
    // See ThemeViewModel.kt
    val themeViewModel = koinViewModel<ThemeViewModel>()
    // The following line converts the StateFlow contained in the ViewModel
    // to a State object. State objects can trigger recompositions, while
    // StateFlow objects can't. The 'withLifecycle' part ensures this state
    // is destroyed when we leave this Activity.
    val state by themeViewModel.state.collectAsStateWithLifecycle()
    val colorConstants = getThemeColors(themeState = state)
    val authenticationViewModel = koinViewModel<AuthenticationViewModel>()
    BasicScreenWithTheme(
        state = state,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val blockingCredentialsFetcher = BlockingCredentialsFetcher(koinInject())

            /* This should block the UI thread until the "asynchronous" fetching completes. */
            runBlocking {
                blockingCredentialsFetcher.initCredentials()
            }
            var rememberMe by remember { mutableStateOf(blockingCredentialsFetcher.didUserWantToBeRemembered()) }
            var email by remember {
                mutableStateOf(blockingCredentialsFetcher.getEmail() ?: "")
            }
            var password by remember {
                mutableStateOf(blockingCredentialsFetcher.getPassword() ?: "")
            }
            val imageId =
                if (state.theme == ThemeEnum.Dark) R.drawable.light_writings else R.drawable.dark_writings
            Image(
                painter = painterResource(id = imageId),
                contentDescription = "Appropriate theme image",
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.2f)
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = email,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(80.dp),
                onValueChange = { email = it },
                label = { Text("Email", style = MaterialTheme.typography.headlineMedium) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                ),
                textStyle = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(30.dp))
            OutlinedTextField(
                value = password,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(80.dp),
                onValueChange = { password = it },
                label = { Text("Password", style = MaterialTheme.typography.headlineMedium) },
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                textStyle = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = {
                        rememberMe = it
                        authenticationViewModel.setRememberMe(it)
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.secondary
                    )
                )
                Text("Remember me", color = MaterialTheme.colorScheme.onPrimary)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    handleLogin(
                        email = email,
                        password = password,
                        rememberMe = rememberMe,
                        viewModel = authenticationViewModel,
                        owner = owner,
                        navController = navController,
                        context = context
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .height(60.dp)
                    .fillMaxWidth(0.9f)
                    .background(colorConstants.getButtonBackground()),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                border = BorderStroke(3.dp, MaterialTheme.colorScheme.outline)
            ) {
                Text("Login")
            }
        }
    }
}