package com.example.rangerapp.feature_map.domain.use_case.file

import android.net.Uri
import com.example.rangerapp.feature_map.domain.model.MapEntry
import com.example.rangerapp.feature_map.domain.repository.FileRepository

class ImportJson(private val repository: FileRepository) {

    operator fun invoke(
        uri: Uri,
    ): List<MapEntry> {
        return repository.importJson(uri)

    }

}

