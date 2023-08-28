package com.example.rangerapp.feature_map.domain.repository

import com.example.rangerapp.feature_map.domain.model.OtherObservation
import com.example.rangerapp.feature_map.domain.model.UpdateMapEntry
import java.time.LocalDate
import java.time.LocalDateTime

interface OtherObservationRepository {

    suspend fun getAll(
        createdStartDate: LocalDate?,
        createdEndDate: LocalDate?,
        updatedStartDate: LocalDate?,
        updatedEndDate: LocalDate?,
        searchTerm: String
    ): List<OtherObservation>


    suspend fun getById(id: Int): OtherObservation?

    suspend fun getByIndex(
        createdTimestamp: LocalDateTime,
        latitude: Double,
        longitude: Double
    ): OtherObservation?


    suspend fun insert(otherObservation: OtherObservation)

    suspend fun delete(otherObservations: List<OtherObservation>)

    suspend fun update(updateMapEntry: UpdateMapEntry)

    suspend fun insertIgnore(otherObservation: OtherObservation): Long


}