package com.example.rangerapp.feature_map.domain.use_case.visitor_contact

import com.example.rangerapp.feature_map.domain.model.UpdateMapEntry
import com.example.rangerapp.feature_map.domain.repository.VisitorContactRepository

class UpdateVisitorContactLocation(
    private val repository: VisitorContactRepository
) {

    suspend operator fun invoke(updateMapEntry: UpdateMapEntry) {
        repository.update(updateMapEntry)
    }

}