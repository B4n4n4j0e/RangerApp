package com.example.rangerapp.feature_map.domain.use_case.other_observation

import com.example.rangerapp.feature_map.domain.model.UpdateMapEntry
import com.example.rangerapp.feature_map.domain.repository.OtherObservationRepository

class UpdateObservationLocation(
    private val repository: OtherObservationRepository
) {

    suspend operator fun invoke(updateMapEntry: UpdateMapEntry) {
        repository.update(updateMapEntry)
    }

}