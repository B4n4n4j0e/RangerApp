package com.example.rangerapp.feature_map.domain.use_case.file

import android.net.Uri
import com.example.rangerapp.feature_map.domain.repository.FileRepository

class SaveTempUri(
    private val repository: FileRepository
) {


    operator fun invoke(
        uri: Uri
    ): Uri? {
        return repository.saveTempUri(uri)
    }


}
