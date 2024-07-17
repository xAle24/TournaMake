package com.example.tournaMake.filemanager

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.nio.file.NoSuchFileException
import kotlin.reflect.full.memberProperties

/*
* A data class used to store the names of the directories containing the files
* produced by the application. It is used to encourage centralisation
* of the directory names, so that there's no need to manually manage
* file paths elsewhere.
* */
data class AppDirectoryNames(
    val profileImageDirectoryName: String = "tournamake_profile_image"
)

fun doesDirectoryExist(dirName: AppDirectoryNames, context: Context): Boolean {
    val path = "${context.filesDir.path}/${dirName.profileImageDirectoryName}"
    val dir = File(path)
    return dir.isDirectory && dir.exists()
}

fun createDirectory(dirName: AppDirectoryNames, context: Context) {
    val path = "${context.filesDir.path}/${dirName.profileImageDirectoryName}"
    if (!doesDirectoryExist(dirName, context)) {
        val dir = File(path)
        if (dir.mkdir()) {
            Log.d("DEV", "Successfully created directory with file path: $path.")
        } else {
            throw NoSuchFileException("Could not create directory with file path: $path.")
        }
    }
}

fun saveImageToDirectory(bitmap: Bitmap, context: Context, dirName: AppDirectoryNames, fileName: String): Boolean {
    val path = "${context.filesDir.path}/${dirName.profileImageDirectoryName}/$fileName"
    val outputStream = FileOutputStream(path) // creates an OutputStream to write the specified path
    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)) {
        Log.d("DEV", "Something wrong occurred while trying to save an image to path $path.")
        return false
    }
    return true
}

fun getImageFromDirectory(dirName: AppDirectoryNames, imageName: String, context: Context): Uri? {
    val path = "${context.filesDir.path}/${dirName.profileImageDirectoryName}/$imageName"
    val file = File(path)
    if (!file.exists()) {
        Log.d("DEV", "File ${file.path} does not exist.")
        return null
    }
    return Uri.parse(path)
}

/**
 * Uses reflection to iterate through all the fields of the data class [AppDirectoryNames].
 * If the specified file name is in any of the directories, this function returns the first
 * of the directories containing the file.
 * @param fileName: the name of the file
 * @param context: the context of the application; you can get it in activities by writing 'baseContext' (it's a class property, not a method).
 * @return A file path to the requested file, or null if none of the directories in [AppDirectoryNames] contains
 * such file.
 * */
fun searchDirectoriesForFile(fileName: String, context: Context): String? {
    val appDirectories = AppDirectoryNames()

    // Get the KClass of the data class
    val kClass = appDirectories::class

    // Iterate through all properties
    val dirName = kClass.memberProperties.find { property ->
        val possiblePath = "${context.filesDir.path}/${property.getter.call() as String}/$fileName"
        val file = File(possiblePath)
        file.exists()
    }?.getter?.call() as String?
    Log.d("DEV", "The strange reflection function in FileUtils.kt found that the directory" +
            "containing $fileName is $dirName.")
    return if (dirName != null) "${context.filesDir.path}/$dirName/$fileName" else null
}
