package com.example.rangerapp.feature_map.domain.use_case.biotop

import com.example.rangerapp.feature_map.domain.model.Biotop
import com.example.rangerapp.feature_map.domain.repository.BiotopRepository
import java.time.LocalDateTime

class GetBiotop(
    private val repository: BiotopRepository
) {
    suspend operator fun invoke(id: Int): Biotop? {
        return repository.getById(id)
    }

    suspend operator fun invoke(
        createdTimestamp: LocalDateTime,
        latitude: Double,
        longitude: Double
    ): Biotop? {
        return repository.getByIndex(createdTimestamp, latitude, longitude)
    }
}