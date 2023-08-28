package com.example.rangerapp.feature_map.domain.use_case.other_observation

import com.example.rangerapp.feature_map.domain.model.OtherObservation
import com.example.rangerapp.feature_map.domain.repository.OtherObservationRepository

class DeleteOtherObservations(
    private val repository: OtherObservationRepository
) {

    suspend operator fun invoke(otherObservations: List<OtherObservation>) {
        repository.delete(otherObservations)
    }

}