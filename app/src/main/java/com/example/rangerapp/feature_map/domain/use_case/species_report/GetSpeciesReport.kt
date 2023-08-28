package com.example.rangerapp.feature_map.domain.use_case.species_report

import com.example.rangerapp.feature_map.domain.model.SpeciesReport
import com.example.rangerapp.feature_map.domain.repository.SpeciesReportRepository
import java.time.LocalDateTime

class GetSpeciesReport(
    private val repository: SpeciesReportRepository
) {
    suspend operator fun invoke(id: Int): SpeciesReport? {
        return repository.getById(id)
    }

    suspend operator fun invoke(
        createdTimestamp: LocalDateTime,
        latitude: Double,
        longitude: Double
    ): SpeciesReport? {
        return repository.getByIndex(createdTimestamp, latitude, longitude)
    }
}