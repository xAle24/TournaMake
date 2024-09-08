package com.example.tournaMake.utils

import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.tournaMake.R
import com.example.tournaMake.filemanager.AppDirectoryNames
import com.example.tournaMake.filemanager.createDirectory
import com.example.tournaMake.filemanager.doesDirectoryExist
import com.example.tournaMake.ui.screens.profile.createImageRequest
import java.io.File

interface PicturePicker {
    val pickedImage: Uri
    fun pickImage()
    fun saveImageToDirectory(dirName: String)
}

/* TODO: integrate this in Alessio's screen */
@Composable
fun rememberPicturePicker(
    saveImage: (Uri) -> Unit,
    contentResolver: ContentResolver,
): PicturePicker {
    val ctx = LocalContext.current
    val imageUri = remember {
        val imageFile = File.createTempFile("tmp_image", ".jpg", ctx.externalCacheDir)
        FileProvider.getUriForFile(ctx, ctx.packageName + ".provider", imageFile)
    }
    var capturedImageUri by remember { mutableStateOf(Uri.EMPTY) }
    val singlePhotoPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { pickedPicture ->
            if (pickedPicture != null) {
                capturedImageUri = imageUri
                saveImage(capturedImageUri)
            }
        }

    val photoPicker by remember {
        derivedStateOf {
            object : PicturePicker {
                override val pickedImage = capturedImageUri

                override fun pickImage() = singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(
                        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )

                override fun saveImageToDirectory(dirName: String) {
                    if (!doesDirectoryExist(dirName, ctx)) {
                        createDirectory(dirName, ctx)
                    }
                    val inputStream = contentResolver.openInputStream(pickedImage)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    com.example.tournaMake.filemanager.saveImageToDirectory(
                        bitmap = bitmap,
                        context = ctx,
                        dirName = dirName
                    )
                }
            }
        }
    }
    return photoPicker
}

@Composable
fun PictureBox(
    picturePicker: PicturePicker,
    directoryName: String
) {
    if (picturePicker.pickedImage == Uri.EMPTY) {
        Image(
            painter = painterResource(id = R.drawable.no_game_picture),
            contentDescription = "Avatar",
            modifier = Modifier
                .padding(4.dp)
                .width(150.dp)
                .height(150.dp)
                .clickable {
                    picturePicker.pickImage()
                    picturePicker.saveImageToDirectory(directoryName)
                }
        )
    } else {
        AsyncImage(
            model = picturePicker.pickedImage,
            contentDescription = null,
            modifier = Modifier
                .padding(4.dp)
                .width(150.dp)
                .height(150.dp)
                .clickable {
                    picturePicker.pickImage()
                    picturePicker.saveImageToDirectory(directoryName)
                },
            contentScale = ContentScale.FillBounds
        )
    }
}