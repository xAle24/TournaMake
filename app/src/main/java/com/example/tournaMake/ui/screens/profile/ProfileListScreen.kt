package com.example.tournaMake.ui.screens.profile

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tournaMake.R
import com.example.tournaMake.activities.fetchAndUpdateGuestProfiles
import com.example.tournaMake.activities.fetchAndUpdateProfile
import com.example.tournaMake.activities.navgraph.NavigationRoute
import com.example.tournaMake.data.models.AuthenticationViewModel
import com.example.tournaMake.data.models.ProfileListViewModel
import com.example.tournaMake.data.models.ProfileViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.filemanager.AppDirectoryNames
import com.example.tournaMake.filemanager.PROFILE_PICTURE_NAME
import com.example.tournaMake.filemanager.ProfileImageHelperImpl
import com.example.tournaMake.filemanager.doesDirectoryContainFile
import com.example.tournaMake.filemanager.loadImageUriFromDirectory
import com.example.tournaMake.ui.screens.common.BasicScreenWithAppBars
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileListScreen(
    owner: LifecycleOwner,
    navController: NavController
) {
    val context = LocalContext.current
    ProfileImageHelperImpl()
    val themeViewModel = koinViewModel<ThemeViewModel>()
    val state by themeViewModel.state.collectAsStateWithLifecycle()
    val profileViewModel = koinViewModel<ProfileViewModel>()
    // View Model of profiles list
    val profileListViewModel = koinViewModel<ProfileListViewModel>()
    fetchAndUpdateGuestProfiles(profileListViewModel, owner)
    val profileNamesList = profileListViewModel.profileNamesListLiveData.observeAsState()
    val authenticationViewModel = koinViewModel<AuthenticationViewModel>()
    val loggedEmail = authenticationViewModel.loggedEmail.collectAsStateWithLifecycle()
    var selectedImageURI by remember {
        mutableStateOf(
            if (doesDirectoryContainFile(
                    AppDirectoryNames.profileImageDirectoryName,
                    PROFILE_PICTURE_NAME,
                    context,
                    loggedEmail.value.loggedProfileEmail
                )
            ) loadImageUriFromDirectory(
                AppDirectoryNames.profileImageDirectoryName,
                PROFILE_PICTURE_NAME,
                context,
                loggedEmail.value.loggedProfileEmail
            ) else null
        )
    }
    fetchAndUpdateProfile(
        loggedEmail.value.loggedProfileEmail,
        profileViewModel,
        thenUpdateImageUri = { selectedImageURI = it },
        baseContext = context,
        owner = owner
    )

    BasicScreenWithAppBars(
        state = state,
        backFunction = { navController.navigate(NavigationRoute.MenuScreen.route) },
        showTopBar = true,
        showBottomBar = false
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .clickable { navController.navigate(NavigationRoute.ProfileScreen.route) },
                colors = CardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                    disabledContentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileListImage(selectedImage = selectedImageURI)
                    Text(
                        profileViewModel.profileLiveData.value?.username ?: "Loading...",
                        modifier = Modifier.padding(0.dp, 10.dp),
                        style = MaterialTheme.typography.displaySmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(1f)
                    .background(MaterialTheme.colorScheme.secondary)
            ) {
                if (profileNamesList.value != null) {
                    items(profileNamesList.value!!) { item ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { /* Do something when button is clicked */ },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(60.dp)
                        ) {
                            Text(item)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun ProfileListImage(
    selectedImage: Uri?,
) {
    key(selectedImage) {
        if (selectedImage == null) {
            Image(
                painter = painterResource(id = R.drawable.no_profile_picture_icon),
                contentDescription = "Avatar",
                modifier = Modifier
                    .padding(4.dp)
                    .width(85.dp)
                    .height(85.dp)
            )
        } else {
            AsyncImage(
                model = createImageRequest(LocalContext.current, selectedImage),
                contentDescription = null,
                modifier = Modifier
                    .padding(12.dp)
                    .width(85.dp)
                    .height(85.dp),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}