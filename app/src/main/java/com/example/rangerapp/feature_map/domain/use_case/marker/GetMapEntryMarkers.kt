package com.example.rangerapp.feature_map.domain.use_case.marker

import com.example.rangerapp.feature_map.domain.model.MapEntry
import com.example.rangerapp.feature_map.domain.model.MapEntryMarker
import com.example.rangerapp.feature_map.domain.repository.MapEntryMarkerRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class GetMapEntryMarkers(
    private val repository: MapEntryMarkerRepository
) {

    operator fun invoke(
        filter: Set<MapEntry.MapEntryType>,
        startCreatedDate: LocalDate?,
        endCreatedDate: LocalDate?,
        startUpdatedDate: LocalDate?,
        endUpdatedDate: LocalDate?,
        searchTerm: String,
        startControlDate: LocalDate?,
        endControlDate: LocalDate?
    ): Flow<List<MapEntryMarker>> {
        return repository.getAll(
            filter,
            startCreatedDate,
            endCreatedDate,
            startUpdatedDate,
            endUpdatedDate,
            searchTerm,
            startControlDate,
            endControlDate
        )
    }

}
