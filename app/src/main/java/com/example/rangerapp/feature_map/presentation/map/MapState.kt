package com.example.rangerapp.feature_map.presentation.map

import com.example.rangerapp.feature_map.domain.model.MapEntryMarker

data class MapState(
    val isFollowing: Boolean = true,
    val dragMode: Boolean = false,
    val mapEntryMarkers: List<MapEntryMarker> = emptyList(),
)