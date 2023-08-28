package com.example.rangerapp.feature_map.presentation.map

import com.example.rangerapp.feature_map.domain.model.MapEntry
import java.time.LocalDate

sealed class MapEvent {
    data class MoveMarker(
        val id: Int,
        val latitude: Double,
        val longitude: Double,
        val mapEntryType: MapEntry.MapEntryType
    ) : MapEvent()

    data class AddMarker(val latitude: Double, val longitude: Double) : MapEvent()
    object ToggleFollowMap : MapEvent()
    object ToggleDragMode : MapEvent()
    data class ActivateFilter(
        val mapEntryTypes: Set<MapEntry.MapEntryType>,
        val startCreatedDate: LocalDate?,
        val endCreatedDate: LocalDate?,
        val startUpdatedDate: LocalDate?,
        val endUpdatedDate: LocalDate?,
        val searchTerm: String,
        val startControlDate: LocalDate?,
        val endControlDate: LocalDate?
    ) : MapEvent()


}