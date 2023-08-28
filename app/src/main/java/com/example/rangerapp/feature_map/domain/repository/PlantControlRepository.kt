package com.example.rangerapp.feature_map.domain.repository

import com.example.rangerapp.feature_map.domain.model.PlantControl
import com.example.rangerapp.feature_map.domain.model.UpdateMapEntry
import java.time.LocalDate
import java.time.LocalDateTime

interface PlantControlRepository {

    suspend fun getAll(
        createdStartDate: LocalDate?,
        createdEndDate: LocalDate?,
        updatedStartDate: LocalDate?,
        updatedEndDate: LocalDate?,
        searchTerm: String
    ): List<PlantControl>

    suspend fun getByIndex(
        createdTimestamp: LocalDateTime,
        latitude: Double,
        longitude: Double
    ): PlantControl?

    suspend fun getById(id: Int): PlantControl?

    suspend fun insert(plantControl: PlantControl)

    suspend fun delete(plantControls: List<PlantControl>)

    suspend fun update(updateMapEntry: UpdateMapEntry)

    suspend fun insertIgnore(plantControl: PlantControl): Long

}