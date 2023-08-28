package com.example.rangerapp.feature_map.domain.use_case.monitoring

import com.example.rangerapp.feature_map.domain.model.Monitoring
import com.example.rangerapp.feature_map.domain.model.PlantControl
import com.example.rangerapp.feature_map.domain.repository.MonitoringRepository
import com.example.rangerapp.feature_map.domain.repository.PlantControlRepository

class DeleteMonitoring(
    private val repository: MonitoringRepository
) {

    suspend operator fun invoke(monitoringList: List<Monitoring>) {
        repository.delete(monitoringList)
    }

}