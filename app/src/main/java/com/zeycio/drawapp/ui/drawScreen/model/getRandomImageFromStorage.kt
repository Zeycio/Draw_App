package com.zeycio.drawapp.ui.drawScreen.model

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Log



// Function to fetch a random image from device storage
fun getRandomImageFromStorage(context: Context): Bitmap? {
    try {
        val contentResolver: ContentResolver = context.contentResolver
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(MediaStore.Images.Media._ID)

        // Get all image IDs
        val cursor = contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )

        val imageIds = mutableListOf<Long>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                imageIds.add(id)
            }
        }

        // If no images found, create a fallback bitmap
        if (imageIds.isEmpty()) {
            return createFallbackBitmap()
        }

        // Get a random image ID
        val randomId = imageIds.random()
        val imageUri = android.net.Uri.withAppendedPath(uri, randomId.toString())

        try {
            val inputStream = contentResolver.openInputStream(imageUri)
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            Log.e("ImageLoader", "Error loading image", e)
            // Fallback to a generated bitmap if loading fails
            return createFallbackBitmap()
        }
    } catch (e: Exception) {
        Log.e("ImageLoader", "Error in image loading process", e)
        return createFallbackBitmap()
    }
}