package com.example.rangerapp.feature_map.domain.use_case.monitoring

import com.example.rangerapp.feature_map.domain.model.UpdateMapEntry
import com.example.rangerapp.feature_map.domain.repository.MonitoringRepository

class UpdateMonitoringLocation(
    private val repository: MonitoringRepository
) {

    suspend operator fun invoke(updateMapEntry: UpdateMapEntry) {
        repository.update(updateMapEntry)
    }

}