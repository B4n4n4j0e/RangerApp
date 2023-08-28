package com.example.rangerapp.feature_map.domain.use_case.biotop

import com.example.rangerapp.feature_map.domain.model.UpdateMapEntry
import com.example.rangerapp.feature_map.domain.repository.BiotopRepository

class UpdateBiotopLocation(
    private val repository: BiotopRepository
) {

    suspend operator fun invoke(updateMapEntry: UpdateMapEntry) {
        repository.update(updateMapEntry)
    }

}