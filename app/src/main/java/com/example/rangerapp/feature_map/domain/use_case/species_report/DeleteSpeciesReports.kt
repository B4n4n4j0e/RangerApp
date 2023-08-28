package com.example.rangerapp.feature_map.domain.use_case.species_report

import com.example.rangerapp.feature_map.domain.model.SpeciesReport
import com.example.rangerapp.feature_map.domain.repository.SpeciesReportRepository

class DeleteSpeciesReports(
    private val repository: SpeciesReportRepository
) {

    suspend operator fun invoke(speciesReport: List<SpeciesReport>) {
        repository.delete(speciesReport)
    }

}