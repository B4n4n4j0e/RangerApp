package com.example.rangerapp.feature_map.domain.repository

import com.example.rangerapp.feature_map.domain.model.Biotop
import com.example.rangerapp.feature_map.domain.model.UpdateMapEntry
import java.time.LocalDate
import java.time.LocalDateTime

interface BiotopRepository {

    suspend fun getAll(
        createdStartDate: LocalDate?,
        createdEndDate: LocalDate?,
        updatedStartDate: LocalDate?,
        updatedEndDate: LocalDate?,
        searchTerm: String
    ): List<Biotop>

    suspend fun getByIndex(
        createdTimestamp: LocalDateTime,
        latitude: Double,
        longitude: Double
    ): Biotop?

    suspend fun getById(id: Int): Biotop?

    suspend fun insert(biotop: Biotop)

    suspend fun delete(biotopList: List<Biotop>)

    suspend fun update(updateMapEntry: UpdateMapEntry)

    suspend fun insertIgnore(biotop: Biotop): Long

}