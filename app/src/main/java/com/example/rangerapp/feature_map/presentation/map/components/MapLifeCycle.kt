package com.example.rangerapp.feature_map.presentation.map

import android.location.LocationManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.rangerapp.BuildConfig
import com.example.rangerapp.R
import com.example.rangerapp.feature_map.domain.model.MapEntry
import com.example.rangerapp.feature_map.presentation.map_entries.MapViewModel
import com.example.rangerapp.feature_map.presentation.util.CustomFormat
import com.example.rangerapp.feature_map.presentation.util.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Marker.OnMarkerDragListener
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun rememberMapViewWithLifecycle(
    viewModel: MapViewModel,
    navController: NavController
): MapView {
    val context = LocalContext.current
    var lastZoomEventTime = 0L
    val mapState = viewModel.mapState
    val mapView = remember {
        MapView(context).apply {
            this.setMultiTouchControls(true)
            id = R.id.map
            clipToOutline = true
            Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
            val provider = GpsMyLocationProvider(context)
            provider.addLocationSource(LocationManager.NETWORK_PROVIDER)
            overlays.add(
                0, MyLocationNewOverlay(
                    provider, this
                )
            )

        }

    }

    val locationOverlay = mapView.overlays[0] as MyLocationNewOverlay

    locationOverlay.enableMyLocation();
    viewModel.setLocationOverlay(locationOverlay)

    // Makes MapView follow the lifecycle of this composable
    val lifecycleObserver = rememberMapLifecycleObserver(mapView, viewModel = viewModel)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }


    LaunchedEffect(key1 = mapState.value.isFollowing) {
        if (mapState.value.isFollowing) {
            locationOverlay.enableFollowLocation()
        } else {
            locationOverlay.disableFollowLocation()
        }
    }


    LaunchedEffect(key1 = mapState.value.mapEntryMarkers) {
        withContext(Dispatchers.Main) {
            mapView.overlays.forEach {
                if (it is Marker) {
                    mapView.overlays.remove(it)
                }
            }
            mapState.value.mapEntryMarkers.forEach { mapEntry ->
                val marker = Marker(mapView)
                marker.position = GeoPoint(mapEntry.latitude, mapEntry.longitude)
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                marker.title =
                    mapEntry.title + CustomFormat.formatLocalDateTimeShort(mapEntry.createdTimestamp)
                marker.id = mapEntry.id.toString()
                marker.subDescription = mapEntry.description

                when (mapEntry.mapEntryType) {
                    MapEntry.MapEntryType.VISITOR_CONTACT -> {
                        marker.icon = createAndScaleIcon(
                            context,
                            R.drawable.pin_visitors, 0.7
                        )
                    }

                    MapEntry.MapEntryType.OTHER_OBSERVATION -> {
                        marker.icon = createAndScaleIcon(
                            context,
                            R.drawable.pin_other_observations, 0.7
                        )
                    }

                    MapEntry.MapEntryType.SPECIES_REPORT -> {
                        marker.icon = createAndScaleIcon(
                            context,
                            R.drawable.pin_species, 0.7
                        )
                    }

                    MapEntry.MapEntryType.PLANT_CONTROL -> {
                        marker.icon = createAndScaleIcon(
                            context,
                            R.drawable.pin_plant_control, 0.7
                        )
                    }

                    MapEntry.MapEntryType.MONITORING -> {
                        marker.icon = createAndScaleIcon(
                            context,
                            R.drawable.pin_monitoring, 0.7
                        )
                    }

                    MapEntry.MapEntryType.BIOTOP -> {
                        marker.icon = createAndScaleIcon(
                            context,
                            R.drawable.pin_biotop, 0.7
                        )
                    }
                }

                marker.setOnMarkerClickListener { activeMarker, _ ->
                    if (activeMarker.isInfoWindowShown) {
                        viewModel.onEvent(
                            MapEvent.AddMarker(
                                activeMarker.position.latitude,
                                activeMarker.position.longitude
                            )
                        )
                        navController.navigate(
                            Screen.AddEditMapEntryScreen.route +
                                    "?mapEntryId=${activeMarker.id}&" +
                                    "mapEntryType=${mapEntry.mapEntryType}"
                        )
                    } else {
                        activeMarker.showInfoWindow()
                    }
                    true
                }

                val markOnDragListener = object : OnMarkerDragListener {
                    override fun onMarkerDrag(marker: Marker?) {
                    }

                    override fun onMarkerDragEnd(marker: Marker?) {
                        if (marker != null && mapState.value.dragMode) {
                            viewModel.onEvent(
                                MapEvent.MoveMarker(
                                    marker.id.toInt(),
                                    latitude = marker.position.latitude,
                                    longitude = marker.position.longitude,
                                    mapEntryType = mapEntry.mapEntryType
                                )
                            )
                        }

                    }

                    override fun onMarkerDragStart(marker: Marker?) {

                    }

                }
                marker.isDraggable = true

                marker.setOnMarkerDragListener(markOnDragListener)

                mapView.overlays.add(marker)
            }



            mapView.controller.setZoom(15.0)

            val mapListener = object : MapListener {
                override fun onScroll(event: ScrollEvent?): Boolean {
                    if (mapState.value.isFollowing && !viewModel.locationOverlay.value?.isFollowLocationEnabled!!) {
                        viewModel.onEvent(MapEvent.ToggleFollowMap)
                    }
                    if (!mapState.value.isFollowing && viewModel.locationOverlay.value?.isFollowLocationEnabled!!) {
                        viewModel.onEvent(MapEvent.ToggleFollowMap)
                    }
                    return true
                }

                override fun onZoom(event: ZoomEvent?): Boolean {
                    lastZoomEventTime = System.currentTimeMillis()
                    return true
                }

            }
            val mapEventsReceiver = object : MapEventsReceiver {

                override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                    return false
                }

                override fun longPressHelper(p: GeoPoint?): Boolean {

                    val timeDiff = System.currentTimeMillis() - lastZoomEventTime
                    if (p != null && timeDiff > 500 && !mapState.value.dragMode) {
                        viewModel.onEvent(
                            MapEvent.AddMarker(
                                latitude = p.latitude,
                                longitude = p.longitude
                            )
                        )
                        navController.navigate(
                            Screen.AddEditMapEntryScreen.route +
                                    "?latitude=${p.latitude}&" +
                                    "longitude=${p.longitude}"
                        )

                    }

                    return false
                }
            }

            val mapEventOverlay = MapEventsOverlay(mapEventsReceiver)
            mapView.overlays.add(mapEventOverlay);
            mapView.addMapListener(mapListener)
            mapView.invalidate()
        }
    }

    return mapView
}

@Composable
fun rememberMapLifecycleObserver(
    mapView: MapView, viewModel: MapViewModel,
): LifecycleEventObserver =
    remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    mapView.controller.setCenter(viewModel.lastLocation.value)
                }

                Lifecycle.Event.ON_RESUME -> {
                    mapView.onResume()
                    mapView.controller.setCenter(viewModel.lastLocation.value)
                }

                Lifecycle.Event.ON_PAUSE -> {
                    mapView.onPause()
                }

                else -> {
                }
            }
        }
    }