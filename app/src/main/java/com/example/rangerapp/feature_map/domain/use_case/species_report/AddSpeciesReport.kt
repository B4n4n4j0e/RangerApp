package com.example.rangerapp.feature_map.domain.use_case.species_report

import com.example.rangerapp.feature_map.domain.model.InvalidSpeciesReportException
import com.example.rangerapp.feature_map.domain.model.SpeciesReport
import com.example.rangerapp.feature_map.domain.repository.SpeciesReportRepository

class AddSpeciesReport(
    private val repository: SpeciesReportRepository
) {

    @kotlin.jvm.Throws(InvalidSpeciesReportException::class)
    suspend operator fun invoke(speciesReport: SpeciesReport) {

        if (speciesReport.species.isEmpty()) {
            throw InvalidSpeciesReportException("Das Feld Art darf nicht leer sein.")
        }
        if (speciesReport.quantity <= 0) {
            throw InvalidSpeciesReportException("Der Wert im Feld Anzahl muss größer als 0 sein.")
        }

        repository.insert(speciesReport)


    }

}