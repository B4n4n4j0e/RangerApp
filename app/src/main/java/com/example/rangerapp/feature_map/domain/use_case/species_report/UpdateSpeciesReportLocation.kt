package com.example.rangerapp.feature_map.domain.use_case.species_report

import com.example.rangerapp.feature_map.domain.model.UpdateMapEntry
import com.example.rangerapp.feature_map.domain.repository.SpeciesReportRepository

class UpdateSpeciesReportLocation(
    private val repository: SpeciesReportRepository
) {

    suspend operator fun invoke(updateMapEntry: UpdateMapEntry) {
        repository.update(updateMapEntry)
    }

}