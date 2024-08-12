package com.example.tournaMake.ui.screens.registration

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.tournaMake.R
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme
import com.example.tournaMake.ui.theme.getThemeColors
import com.example.tournaMake.utils.Coordinates
import com.example.tournaMake.utils.LocationService
import com.example.tournaMake.utils.rememberCameraLauncher

@Composable
fun RegistrationPhotoScreen(
    state: ThemeState,
    back: () -> Unit,
    loadMenu: () -> Unit,
    selectedImage: Uri?,
    photoPickerLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    snackbarHostState: SnackbarHostState,
    requestLocation: () -> Unit,
    coordinatesLiveData: LiveData<Coordinates>
) {
    val configuration = LocalConfiguration.current // used to find screen size
    // val screenHeight = configuration.screenHeightDp
    val screenWidth = configuration.screenWidthDp
    val coordinates = coordinatesLiveData.observeAsState()
    // Camera launcher; code taken from tutor Gianni
    val cameraLauncher = rememberCameraLauncher()

    BasicScreenWithTheme(state = state) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            key(selectedImage) {
                if (selectedImage != null) {
                    /* The profile image will be contained here. */
                    AsyncImage(
                        model = createImageRequest(LocalContext.current, selectedImage),
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                BorderStroke(4.dp, MaterialTheme.colorScheme.primary)
                            ),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.no_profile_picture_icon),
                        contentDescription = "Appropriate logo image",
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                BorderStroke(4.dp, MaterialTheme.colorScheme.primary)
                            ),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Button(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .width((screenWidth * 0.8).dp)
                    .height(85.dp)
            ) {
                Text(
                    text = "Upload Photo",
                    fontSize = 30.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Adding button to take picture with Camera
            Button(
                onClick = {
                          cameraLauncher.captureImage()
                },
                modifier = Modifier
                    .width((screenWidth * 0.8).dp)
                    .height(85.dp)
            ) {
                Text(
                    text = "Take picture",
                    fontSize = 30.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            LocationUIArea(
                snackbarHostState = snackbarHostState,
                requestLocation = requestLocation,
                state = state,
                coordinates = coordinates.value
            )
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

/**
 * This is needed because oftentimes newUri is equal to the value saved in
 * the "selectedImage" var in the setContent function of this activity,
 * so the composables don't "feel the urge" to update the image.
 * */
fun createImageRequest(context: Context, uri: Uri): ImageRequest {
    return ImageRequest.Builder(context)
        .data(uri)
        .diskCachePolicy(CachePolicy.DISABLED)
        .memoryCachePolicy(CachePolicy.DISABLED)
        .build()
}

@Composable
fun LocationUIArea(
    snackbarHostState: SnackbarHostState,
    requestLocation: () -> Unit,
    state: ThemeState,
    coordinates: Coordinates?
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(150.dp)
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            Button(onClick = {
                requestLocation()
            }) {
                Text("Get current location")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Latitude: ${coordinates?.latitude ?: "-"}", color = getThemeColors(
                    themeState = state
                ).getNormalTextColor()
            )
            Text(
                "Longitude: ${coordinates?.longitude ?: "-"}",
                color = getThemeColors(
                    themeState = state
                ).getNormalTextColor()
            )
        }
    }
}


