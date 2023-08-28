package com.example.rangerapp.feature_map.presentation.map_entries

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rangerapp.feature_map.domain.model.MapEntry
import com.example.rangerapp.feature_map.domain.model.UpdateMapEntry
import com.example.rangerapp.feature_map.domain.use_case.biotop.BiotopUseCases
import com.example.rangerapp.feature_map.domain.use_case.marker.MapEntryMarkerUseCases
import com.example.rangerapp.feature_map.domain.use_case.monitoring.MonitoringUseCases
import com.example.rangerapp.feature_map.domain.use_case.other_observation.OtherObservationUseCases
import com.example.rangerapp.feature_map.domain.use_case.plant_control.PlantControlUseCases
import com.example.rangerapp.feature_map.domain.use_case.species_report.SpeciesReportUseCases
import com.example.rangerapp.feature_map.domain.use_case.visitor_contact.VisitorContactUseCases
import com.example.rangerapp.feature_map.presentation.map.MapEvent
import com.example.rangerapp.feature_map.presentation.map.MapState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val mapEntryMarkerUseCases: MapEntryMarkerUseCases,
    private val otherObservationUseCases: OtherObservationUseCases,
    private val plantControlUseCases: PlantControlUseCases,
    private val speciesReportUseCases: SpeciesReportUseCases,
    private val visitorContactUseCases: VisitorContactUseCases,
    private val biotopUseCases: BiotopUseCases,
    private val monitoringUseCases: MonitoringUseCases
) : ViewModel() {

    private val _mapState = mutableStateOf(MapState())
    val mapState: State<MapState> = _mapState

    private val _locationOverlay = mutableStateOf<MyLocationNewOverlay?>(null)
    val locationOverlay: State<MyLocationNewOverlay?> = _locationOverlay
    private val _lastLocation = mutableStateOf(GeoPoint(0.0, 0.0))
    val lastLocation: State<GeoPoint> = _lastLocation
    private var getMarkersJob: Job? = null


    fun setLocationOverlay(locationOverlay: MyLocationNewOverlay) {
        _locationOverlay.value = locationOverlay
    }

    fun onEvent(event: MapEvent) {

        when (event) {
            is MapEvent.AddMarker -> {
                _lastLocation.value = GeoPoint(event.latitude, event.longitude)
            }

            is MapEvent.MoveMarker -> {
                viewModelScope.launch {
                    when (event.mapEntryType) {
                        MapEntry.MapEntryType.SPECIES_REPORT -> speciesReportUseCases.updateLocation(
                            UpdateMapEntry(
                                event.id,
                                latitude = event.latitude,
                                longitude = event.longitude,
                            )
                        )

                        MapEntry.MapEntryType.VISITOR_CONTACT -> visitorContactUseCases.updateLocation(
                            UpdateMapEntry(
                                event.id,
                                latitude = event.latitude,
                                longitude = event.longitude,
                            )
                        )

                        MapEntry.MapEntryType.PLANT_CONTROL -> plantControlUseCases.updateLocation(
                            UpdateMapEntry(
                                event.id,
                                latitude = event.latitude,
                                longitude = event.longitude,
                            )
                        )

                        MapEntry.MapEntryType.OTHER_OBSERVATION -> otherObservationUseCases.updateLocation(
                            UpdateMapEntry(
                                event.id,
                                latitude = event.latitude,
                                longitude = event.longitude,
                            )
                        )

                        MapEntry.MapEntryType.MONITORING -> monitoringUseCases.updateLocation(
                            UpdateMapEntry(
                                event.id,
                                latitude = event.latitude,
                                longitude = event.longitude,
                            )
                        )

                        MapEntry.MapEntryType.BIOTOP -> biotopUseCases.updateLocation(
                            UpdateMapEntry(
                                event.id,
                                latitude = event.latitude,
                                longitude = event.longitude,
                            )
                        )
                    }
                }
            }

            is MapEvent.ToggleFollowMap -> {
                _mapState.value = mapState.value.copy(
                    isFollowing = !mapState.value.isFollowing
                )
            }


            MapEvent.ToggleDragMode -> {
                _mapState.value = mapState.value.copy(
                    dragMode = !mapState.value.dragMode
                )
            }

            is MapEvent.ActivateFilter -> {
                getMarkers(
                    event.mapEntryTypes,
                    event.startCreatedDate,
                    event.endCreatedDate,
                    event.startUpdatedDate,
                    event.endUpdatedDate,
                    event.searchTerm,
                    event.startControlDate,
                    event.endControlDate
                )
            }
        }
    }

    private fun getMarkers(
        mapFilter: Set<MapEntry.MapEntryType>,
        startCreatedDate: LocalDate?,
        endCreatedDate: LocalDate?,
        startUpdatedDate: LocalDate?,
        endUpdatedDate: LocalDate?,
        searchTerm: String,
        startControlDate: LocalDate?,
        endControlDate: LocalDate?,
    ) {
        getMarkersJob?.cancel()
        getMarkersJob = mapEntryMarkerUseCases.getMapEntryMarkers(
            mapFilter,
            startCreatedDate,
            endCreatedDate,
            startUpdatedDate,
            endUpdatedDate,
            searchTerm,
            startControlDate,
            endControlDate
        )
            .onEach { marker ->
                _mapState.value = mapState.value.copy(
                    mapEntryMarkers = marker
                )
            }.launchIn(viewModelScope)
    }

}