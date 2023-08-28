package com.example.rangerapp.feature_map.domain.use_case.other_observation

import com.example.rangerapp.feature_map.domain.model.OtherObservation
import com.example.rangerapp.feature_map.domain.repository.OtherObservationRepository
import java.time.LocalDateTime

class GetOtherObservation(
    private val repository: OtherObservationRepository
) {
    suspend operator fun invoke(id: Int): OtherObservation? {
        return repository.getById(id)
    }

    suspend operator fun invoke(
        createdTimestamp: LocalDateTime,
        latitude: Double,
        longitude: Double
    ): OtherObservation? {
        return repository.getByIndex(createdTimestamp, latitude, longitude)
    }

}