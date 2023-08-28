package com.example.rangerapp.feature_map.domain.use_case.species_report

import com.example.rangerapp.feature_map.domain.model.SpeciesReport
import com.example.rangerapp.feature_map.domain.repository.SpeciesReportRepository
import java.time.LocalDate

class GetSpeciesReports(
    private val repository: SpeciesReportRepository
) {
    suspend operator fun invoke(
        startCreatedDate: LocalDate?,
        endCreatedDate: LocalDate?,
        startUpdatedDate: LocalDate?,
        endUpdatedDate: LocalDate?,
        searchTerm: String

    ): List<SpeciesReport> {
        return repository.getAll(
            startCreatedDate,
            endCreatedDate,
            startUpdatedDate,
            endUpdatedDate,
            searchTerm
        )
    }

}