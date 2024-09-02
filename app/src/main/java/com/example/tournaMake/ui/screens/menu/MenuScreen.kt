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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.tournaMake.R
import com.example.tournaMake.activities.fetchAndUpdateNotification
import com.example.tournaMake.activities.navgraph.NavigationRoute
import com.example.tournaMake.data.models.AuthenticationViewModel
import com.example.tournaMake.data.models.NotificationViewModel
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme
import com.example.tournaMake.ui.theme.getThemeColors
import org.koin.androidx.compose.koinViewModel

@Composable
fun MenuScreen(
    navController: NavController,
    owner: LifecycleOwner
) {
    // See ThemeViewModel.kt
    val themeViewModel = koinViewModel<ThemeViewModel>()
    // The following line converts the StateFlow contained in the ViewModel
    // to a State object. State objects can trigger recompositions, while
    // StateFlow objects can't. The 'withLifecycle' part ensures this state
    // is destroyed when we leave this Activity.
    val state by themeViewModel.state.collectAsStateWithLifecycle()
    val authenticationViewModel = koinViewModel<AuthenticationViewModel>()
    val loggedEmail = authenticationViewModel.loggedEmail.collectAsStateWithLifecycle()
    val notificationViewModel = koinViewModel<NotificationViewModel>()
    fetchAndUpdateNotification(
        notificationViewModel,
        loggedEmail.value.loggedProfileEmail,
        owner
    )
    val notification = notificationViewModel.notificationLiveData.observeAsState()
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
            if (state.theme == ThemeEnum.Dark) R.drawable.dark_bottom_bar_background else R.drawable.light_bottom_bar_background
        val showDialog = remember { mutableStateOf(false) }
        if (showDialog.value) {
            ShowNotification(
                openDialog = showDialog,
                onDismiss = { showDialog.value = false },
                listItems = notification.value?.map { it.description } ?: emptyList()
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
                modifier = Modifier.fillMaxWidth(0.95f),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                MenuButton(
                    "Logout", { navController.navigate(NavigationRoute.MainScreen.route) }, R.drawable.backicon, state,
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .fillMaxHeight(0.05f)
                )
                MenuButton("", { showDialog.value = true }, R.drawable.bellnotification, state,
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .fillMaxHeight(0.05f)
                )
            }
            Image(
                painter = painterResource(id = imageLogoId),
                contentDescription = "Appropriate logo image",
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.2f)
            )
            MenuButton("Tournament", { navController.navigate(NavigationRoute.TournamentsListScreen.route) }, R.drawable.triangleicon, state)
            MenuButton("Matches", { navController.navigate(NavigationRoute.MatchesListScreen.route) }, R.drawable.d20_dark, state)
            MenuButton("Game list", { navController.navigate(NavigationRoute.GamesListScreen.route) }, R.drawable.listicon, state)
            MenuButton("Profile", { navController.navigate(NavigationRoute.ProfilesListScreen.route) }, R.drawable.profileicon, state)
            MenuButton("Settings", { navController.navigate(NavigationRoute.SettingsScreen.route) }, R.drawable.settingsicon, state)

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
        Spacer(modifier = Modifier.width(15.dp))
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