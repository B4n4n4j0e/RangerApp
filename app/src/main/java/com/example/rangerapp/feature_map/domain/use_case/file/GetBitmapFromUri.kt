package com.example.rangerapp.feature_map.domain.use_case.file

import android.graphics.Bitmap
import android.net.Uri
import com.example.rangerapp.feature_map.domain.repository.FileRepository

class GetBitmapFromUri(
    private val repository: FileRepository
) {

    operator fun invoke(
        uri: Uri,
    ): Bitmap? {
        return repository.getBitmapFromUri(uri)
    }


}
