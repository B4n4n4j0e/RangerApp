package com.example.rangerapp.feature_map.domain.repository

import com.example.rangerapp.feature_map.domain.model.MapEntry
import com.example.rangerapp.feature_map.domain.model.MapEntryMarker
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface MapEntryMarkerRepository {

    fun getAll(
        filter: Set<MapEntry.MapEntryType>, createdStartDate: LocalDate?,
        createdEndDate: LocalDate?,
        updatedStartDate: LocalDate?,
        updatedEndDate: LocalDate?,
        searchTerm: String,
        startControlDate: LocalDate?,
        endControlDate: LocalDate?,
    ): Flow<List<MapEntryMarker>>

}

