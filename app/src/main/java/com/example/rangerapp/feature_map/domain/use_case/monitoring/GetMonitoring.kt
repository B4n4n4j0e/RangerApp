package com.example.rangerapp.feature_map.domain.use_case.monitoring

import com.example.rangerapp.feature_map.domain.model.Monitoring
import com.example.rangerapp.feature_map.domain.repository.MonitoringRepository
import java.time.LocalDateTime

class GetMonitoring(
    private val repository: MonitoringRepository
) {
    suspend operator fun invoke(id: Int): Monitoring? {
        return repository.getById(id)
    }

    suspend operator fun invoke(
        createdTimestamp: LocalDateTime,
        latitude: Double,
        longitude: Double
    ): Monitoring? {
        return repository.getByIndex(createdTimestamp, latitude, longitude)
    }
}