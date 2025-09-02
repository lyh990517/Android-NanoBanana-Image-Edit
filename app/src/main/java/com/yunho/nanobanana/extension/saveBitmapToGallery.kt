package com.yunho.nanobanana.extension

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore

fun Context.saveBitmapToGallery(
    bitmap: android.graphics.Bitmap
): Boolean {
    return try {
        val filename = "nano_gen_${System.currentTimeMillis()}.jpg"
        val fos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            val imageUri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )

            imageUri?.let { contentResolver.openOutputStream(it) }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            if (!imagesDir.exists()) imagesDir.mkdirs()
            val image = java.io.File(imagesDir, filename)
            java.io.FileOutputStream(image)
        }

        fos?.use {
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 95, it)
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
