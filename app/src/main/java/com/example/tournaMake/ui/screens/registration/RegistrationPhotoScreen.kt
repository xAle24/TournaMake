package com.example.tournaMake.ui.screens.registration

import android.content.Context
import android.net.Uri
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.tournaMake.R
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme

@Composable
fun RegistrationPhotoScreen(
    state: ThemeState,
    back: () -> Unit,
    loadMenu: () -> Unit,
    selectedImage: Uri?,
    photoPickerLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>
) {
    val configuration = LocalConfiguration.current // used to find screen size
    // val screenHeight = configuration.screenHeightDp
    val screenWidth = configuration.screenWidthDp

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


