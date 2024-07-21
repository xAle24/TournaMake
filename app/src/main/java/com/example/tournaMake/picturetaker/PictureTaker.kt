package com.example.tournaMake.picturetaker

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

interface PictureTaker {
    val chosenImageUri: Uri
    fun selectPicture()
}

@Composable
fun rememberPictureTaker(
    onPictureTaken: (imageUri: Uri) -> Unit = {}
): PictureTaker {
    val ctx = LocalContext.current
    val imageUri = remember {
        val imageFile = File.createTempFile("tmp_image", ".jpg", ctx.externalCacheDir)
        FileProvider.getUriForFile(ctx, ctx.packageName + ".provider", imageFile)
    }
    var capturedImageUri by remember { mutableStateOf(Uri.EMPTY) }
    val pictureTakerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
        ) { pictureTakenUri ->
            if (pictureTakenUri != null) {
                capturedImageUri = imageUri
                onPictureTaken(capturedImageUri)
            }
        }
    val pictureTaker by remember {
        derivedStateOf {
            object: PictureTaker {
                override val chosenImageUri = capturedImageUri
                override fun selectPicture() = pictureTakerLauncher.launch(PickVisualMediaRequest())
            }
        }
    }
    return pictureTaker
}