package com.example.rangerapp.feature_map.domain.repository

import com.example.rangerapp.feature_map.domain.model.Monitoring
import com.example.rangerapp.feature_map.domain.model.UpdateMapEntry
import java.time.LocalDate
import java.time.LocalDateTime

interface MonitoringRepository {

    suspend fun getAll(
        createdStartDate: LocalDate?,
        createdEndDate: LocalDate?,
        updatedStartDate: LocalDate?,
        updatedEndDate: LocalDate?,
        startControlDate: LocalDate?,
        endControlDate: LocalDate?,
        searchTerm: String
    ): List<Monitoring>

    suspend fun getByIndex(
        createdTimestamp: LocalDateTime,
        latitude: Double,
        longitude: Double
    ): Monitoring?

    suspend fun getById(id: Int): Monitoring?

    suspend fun insert(monitoring: Monitoring)

    suspend fun delete(monitoringList: List<Monitoring>)

    suspend fun update(updateMapEntry: UpdateMapEntry)

    suspend fun insertIgnore(monitoring: Monitoring): Long

}