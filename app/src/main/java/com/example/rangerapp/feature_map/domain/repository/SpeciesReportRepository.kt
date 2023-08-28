package com.example.rangerapp.feature_map.domain.repository

import com.example.rangerapp.feature_map.domain.model.SpeciesReport
import com.example.rangerapp.feature_map.domain.model.UpdateMapEntry
import java.time.LocalDate
import java.time.LocalDateTime

interface SpeciesReportRepository {

    suspend fun getAll(
        createdStartDate: LocalDate?,
        createdEndDate: LocalDate?,
        updatedStartDate: LocalDate?,
        updatedEndDate: LocalDate?,
        searchTerm: String
    ): List<SpeciesReport>

    suspend fun getByIndex(
        createdTimestamp: LocalDateTime,
        latitude: Double,
        longitude: Double
    ): SpeciesReport?

    suspend fun getById(id: Int): SpeciesReport?

    suspend fun insert(speciesReport: SpeciesReport)

    suspend fun delete(speciesReports: List<SpeciesReport>)

    suspend fun update(updateMapEntry: UpdateMapEntry)

    suspend fun insertIgnore(speciesReport: SpeciesReport): Long

}