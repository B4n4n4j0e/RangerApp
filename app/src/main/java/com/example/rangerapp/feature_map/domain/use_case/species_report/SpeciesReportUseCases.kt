package com.example.rangerapp.feature_map.domain.use_case.species_report

data class SpeciesReportUseCases(
    val getSpeciesReports: GetSpeciesReports,
    val deleteSpeciesReport: DeleteSpeciesReports,
    val addSpeciesReport: AddSpeciesReport,
    val addIgnoreSpeciesReport: AddIgnoreSpeciesReport,
    val getSpeciesReport: GetSpeciesReport,
    val updateLocation: UpdateSpeciesReportLocation,
)
