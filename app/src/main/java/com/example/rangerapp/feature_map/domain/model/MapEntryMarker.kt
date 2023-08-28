package com.example.rangerapp.feature_map.domain.model

import java.time.LocalDateTime


data class MapEntryMarker(
    val id: Int? = null,
    val title: String,
    val description: String?,
    val longitude: Double,
    val latitude: Double,
    val mapEntryType: MapEntry.MapEntryType,
    val createdTimestamp: LocalDateTime,

    )


/*
data class CustomMarker(
    val mapView: MapView, val context: Context, val mapEntryType: MapEntry.MapEntryType?
) : Marker(mapView, context)

 */