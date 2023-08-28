package com.example.rangerapp.feature_map.domain.repository

import android.content.IntentSender
import android.graphics.Bitmap
import android.net.Uri
import com.example.rangerapp.feature_map.domain.model.MapEntry

interface FileRepository {
    fun saveTempUri(uri: Uri): Uri?
    fun saveImage(bitmap: Bitmap): Uri?
    fun delete(uri: Uri): IntentSender?
    fun getBitmapFromUri(uri: Uri): Bitmap?
    fun exportJson(mapEntries: List<MapEntry>, withPicture: Boolean = true): Uri?
    fun exportCsv(mapEntries: List<MapEntry>, mapEntryType: MapEntry.MapEntryType): Uri?
    fun importJson(uri: Uri): List<MapEntry>

}

