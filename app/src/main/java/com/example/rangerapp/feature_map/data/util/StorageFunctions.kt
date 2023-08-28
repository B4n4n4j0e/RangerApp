package com.example.rangerapp.feature_map.data.util

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.example.rangerapp.feature_map.data.repository.RangerAppFrontendException
import com.example.rangerapp.feature_map.presentation.util.sdk29AndUp
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class StorageFunctions {

    companion object {
        fun saveImage(bitmap: Bitmap, appContext: Context, name: String = ""): Uri? {
            val fileName = name.ifBlank {
                val timeStamp =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val uuid = UUID.randomUUID().toString()
                "IMG_$timeStamp$uuid.jpg"
            }
            val imageCollection = sdk29AndUp {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val existingImageUri = getExistingUri(appContext, fileName, imageCollection)
            if (existingImageUri != null) {
                return existingImageUri
            }

            val content = this.contentValues(fileName)
            return try {
                appContext.contentResolver.insert(imageCollection, content)?.also { uri ->
                    appContext.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                            throw IOException("Couldn't save bitmap")
                        }
                    }
                } ?: throw IOException("Couldn't create MediaStore entry")
            } catch (e: IOException) {
                throw RangerAppFrontendException("MediaStore Eintrag konnte nicht erstellt werden.")
            }
        }


        fun contentValues(
            fileName: String,
            width: String = "",
            height: String = ""
        ): ContentValues {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            if (width.isNotBlank()) {
                values.put(MediaStore.Images.Media.WIDTH, width)
            }
            if (height.isNotBlank()) {
                values.put(MediaStore.Images.Media.HEIGHT, height)
            }
            return values
        }

        private fun getExistingUri(
            appContext: Context,
            fileName: String,
            imageCollection: Uri
        ): Uri? {
            val projection = arrayOf(MediaStore.Images.Media._ID)
            val selection = MediaStore.Images.Media.DISPLAY_NAME + " = ?"
            val selectionArgs = arrayOf(fileName)
            appContext.contentResolver.query(
                imageCollection,
                projection,
                selection,
                selectionArgs,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val imageId = cursor.getLong(idColumnIndex)
                    val uri: Uri = ContentUris.withAppendedId(
                        imageCollection,
                        imageId
                    )
                    return uri
                }
            }
            return null
        }

    }


}