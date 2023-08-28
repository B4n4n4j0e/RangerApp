package com.example.rangerapp.feature_map.domain.use_case.file

import android.graphics.Bitmap
import android.net.Uri
import com.example.rangerapp.feature_map.domain.repository.FileRepository

class SaveImage(
    private val repository: FileRepository
) {


    operator fun invoke(
        bitmap: Bitmap,
    ): Uri? {
        return repository.saveImage(bitmap)
    }


}
