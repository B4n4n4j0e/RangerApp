package com.example.rangerapp.feature_map.domain.use_case.file

import android.content.IntentSender
import android.net.Uri
import com.example.rangerapp.feature_map.domain.repository.FileRepository

class Delete(private val repository: FileRepository) {

    operator fun invoke(
        uri: Uri,
    ): IntentSender? {
        return repository.delete(uri)

    }

}

