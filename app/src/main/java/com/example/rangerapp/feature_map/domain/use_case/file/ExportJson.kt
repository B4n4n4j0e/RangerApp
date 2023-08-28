package com.example.rangerapp.feature_map.domain.use_case.file

import android.net.Uri
import com.example.rangerapp.feature_map.domain.model.MapEntry
import com.example.rangerapp.feature_map.domain.repository.FileRepository

class ExportJson(private val repository: FileRepository) {

    operator fun invoke(
        mapEntries: List<MapEntry>,
        withPicture: Boolean
    ): Uri? {
        return repository.exportJson(mapEntries, withPicture)

    }

}

