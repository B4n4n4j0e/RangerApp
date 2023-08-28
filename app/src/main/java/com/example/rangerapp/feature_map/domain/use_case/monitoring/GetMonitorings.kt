package com.example.rangerapp.feature_map.domain.use_case.monitoring

import com.example.rangerapp.feature_map.domain.model.Monitoring
import com.example.rangerapp.feature_map.domain.repository.MonitoringRepository
import java.time.LocalDate

class GetMonitorings(
    private val repository: MonitoringRepository
) {
    suspend operator fun invoke(
        startCreatedDate: LocalDate?,
        endCreatedDate: LocalDate?,
        startUpdatedDate: LocalDate?,
        endUpdatedDate: LocalDate?,
        startControlDate: LocalDate?,
        endControlDate: LocalDate?,
        searchTerm: String

    ): List<Monitoring> {
        return repository.getAll(
            startCreatedDate,
            endCreatedDate,
            startUpdatedDate,
            endUpdatedDate,
            startControlDate,
            endControlDate,
            searchTerm
        )
    }

}