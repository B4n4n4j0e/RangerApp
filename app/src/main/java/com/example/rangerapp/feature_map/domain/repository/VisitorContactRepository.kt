package com.example.rangerapp.feature_map.domain.repository

import com.example.rangerapp.feature_map.domain.model.UpdateMapEntry
import com.example.rangerapp.feature_map.domain.model.VisitorContact
import java.time.LocalDate
import java.time.LocalDateTime

interface VisitorContactRepository {

    suspend fun getAll(
        createdStartDate: LocalDate?,
        createdEndDate: LocalDate?,
        updatedStartDate: LocalDate?,
        updatedEndDate: LocalDate?,
        searchTerm: String
    ): List<VisitorContact>

    suspend fun getById(id: Int): VisitorContact?

    suspend fun getByIndex(
        createdTimestamp: LocalDateTime,
        latitude: Double,
        longitude: Double
    ): VisitorContact?

    suspend fun insert(visitorContact: VisitorContact)

    suspend fun delete(visitorContacts: List<VisitorContact>)

    suspend fun update(updateMapEntry: UpdateMapEntry)

    suspend fun insertIgnore(visitorContact: VisitorContact): Long


}