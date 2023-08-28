package com.example.rangerapp.feature_map.presentation.map


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.rangerapp.core.presentation.FilterEvent
import com.example.rangerapp.core.presentation.components.RangeDateSelection
import com.example.rangerapp.feature_map.domain.model.MapEntry
import com.example.rangerapp.feature_map.presentation.map_entries.FilterViewModel
import com.example.rangerapp.feature_map.presentation.map_entries.MapViewModel
import com.example.rangerapp.feature_map.presentation.map_entries.components.FilterSection
import com.example.rangerapp.feature_map.presentation.util.CustomFormat
import com.example.rangerapp.feature_map.presentation.util.Screen

@Composable
fun MapScreen(
    navController: NavController,
    viewModel: MapViewModel = hiltViewModel(),
    filterViewModel: FilterViewModel = hiltViewModel()
) {
    val locationOverlay = viewModel.locationOverlay.value
    val mapState = viewModel.mapState.value
    val mapViewState =
        rememberMapViewWithLifecycle(navController = navController, viewModel = viewModel)

    val startCreatedDate = filterViewModel.startCreatedDate.value
    val endCreatedDate = filterViewModel.endCreatedDate.value
    val startUpdatedDate = filterViewModel.startUpdatedDate.value
    val endUpdatedDate = filterViewModel.endUpdatedDate.value
    val startControlDate = filterViewModel.startControlDate.value
    val endControlDate = filterViewModel.endControlDate.value
    val searchBar = filterViewModel.searchBar.value
    val isFilterSectionVisible = filterViewModel.isFilterSectionVisible.value
    val mapFilter = filterViewModel.mapFilter.value



    BackHandler(isFilterSectionVisible) {
        if (isFilterSectionVisible) {
            filterViewModel.onEvent(FilterEvent.ToggleFilterView)
        } else navController.navigateUp()
    }

    LaunchedEffect(Unit) {
        viewModel.onEvent(
            MapEvent.ActivateFilter(
                filterViewModel.mapFilter.value,
                startCreatedDate,
                endCreatedDate,
                startUpdatedDate,
                endUpdatedDate,
                searchBar,
                startControlDate,
                endControlDate
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        AnimatedVisibility(
            visible = isFilterSectionVisible,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Column(
            ) {

                FilterSection(
                    enabled = true,
                    onFilterChange = {
                        filterViewModel.onEvent(FilterEvent.UpdateMapEntryTypeFilter(it))
                    },
                    selectedMapEntries = mapFilter
                )
                RangeDateSelection(
                    modifier = Modifier,
                    startDate = startCreatedDate?.let { CustomFormat.formatLocalDate(it) } ?: "-",
                    startLabel = "Start Erstelldatum",
                    onStartIconClick = {
                        filterViewModel.onEvent(
                            FilterEvent.ChangeCreatedStartDate(
                                it
                            )
                        )
                    },
                    endDate = endCreatedDate?.let { CustomFormat.formatLocalDate(it) } ?: "-",
                    endDateLabel = "Ende Erstelldatum",
                    onEndIconClick = { filterViewModel.onEvent(FilterEvent.ChangeCreatedEndDate(it)) }
                )

                RangeDateSelection(
                    modifier = Modifier,
                    startDate = startUpdatedDate?.let { CustomFormat.formatLocalDate(it) } ?: "-",
                    startLabel = "Start Aktualisierungsdatum",
                    onStartIconClick = {
                        filterViewModel.onEvent(
                            FilterEvent.ChangeUpdatedStartDate(
                                it
                            )
                        )
                    },
                    endDate = endUpdatedDate?.let { CustomFormat.formatLocalDate(it) } ?: "-",
                    endDateLabel = "Ende Aktualisierungsdatum",
                    onEndIconClick = {
                        filterViewModel.onEvent(
                            FilterEvent.ChangeUpdatedEndDateDate(
                                it
                            )
                        )
                    }
                )
                RangeDateSelection(
                    modifier = Modifier,
                    startDate = startControlDate?.let { CustomFormat.formatLocalDate(it) } ?: "-",
                    startLabel = "Start Kontrolldatum",
                    onStartIconClick = {
                        filterViewModel.onEvent(
                            FilterEvent.ChangeControlStartDate(
                                it
                            )
                        )
                    },
                    endDate = endControlDate?.let { CustomFormat.formatLocalDate(it) } ?: "-",
                    endDateLabel = "Ende Kontrolldatum",
                    onEndIconClick = {
                        filterViewModel.onEvent(
                            FilterEvent.ChangeControlEndDateDate(
                                it
                            )
                        )
                    }
                )
                OutlinedTextField(
                    value = searchBar,
                    onValueChange = {
                        filterViewModel.onEvent(FilterEvent.ChangeSearchBar(it))
                    },
                    label = {
                        Text(text = "Suche")
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Such Icon",

                            )
                    },
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    TextButton(modifier = Modifier.weight(1f), onClick = {
                        viewModel.onEvent(
                            MapEvent.ActivateFilter(
                                filterViewModel.mapFilter.value,
                                startCreatedDate,
                                endCreatedDate,
                                startUpdatedDate,
                                endUpdatedDate,
                                searchBar,
                                startControlDate,
                                endControlDate
                            )
                        )
                        filterViewModel.onEvent(FilterEvent.ToggleFilterView)
                    }) {
                        Text(text = "Neue Filter aktivieren")
                    }
                    TextButton(onClick = {
                        filterViewModel.onEvent(FilterEvent.ClearFilter)
                        viewModel.onEvent(
                            MapEvent.ActivateFilter(
                                MapEntry.MapEntryType.values().toSet(),
                                null,
                                null,
                                null,
                                null,
                                "",
                                null,
                                null
                            )
                        )

                    }, modifier = Modifier.weight(1f)) {
                        Text(text = "Filter löschen")
                    }
                }
            }
        }


        Box(modifier = Modifier.fillMaxSize()) {

            AndroidView(
                { mapViewState },
                modifier = Modifier.fillMaxSize(),
            )

            IconButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = {
                    filterViewModel.onEvent(FilterEvent.ToggleFilterView)
                },
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter"
                )
            }
            IconButton(
                modifier = Modifier.align(Alignment.TopStart),
                onClick = {
                    mapViewState.getMapCenter().let { center ->
                        val longitude = center.longitude
                        val latitude = center.latitude
                        viewModel.onEvent(MapEvent.AddMarker(latitude, longitude))
                        navController.navigate(Screen.ExportScreen.route)
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Upload,
                    contentDescription = "Upload"
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {


                FloatingActionButton(
                    modifier = Modifier.padding(bottom = 12.dp),
                    onClick = {

                        viewModel.onEvent(
                            MapEvent.ToggleDragMode
                        )
                    },

                    backgroundColor = MaterialTheme.colors.background
                )

                {
                    if (mapState.dragMode) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Enable Drag mode",
                            tint = MaterialTheme.colors.primary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = "DisableDragMode",
                            tint = MaterialTheme.colors.primary
                        )
                    }

                }


                FloatingActionButton(
                    modifier = Modifier.padding(bottom = 12.dp),
                    onClick = {
                        viewModel.onEvent(MapEvent.ToggleFollowMap)
                    },

                    backgroundColor = MaterialTheme.colors.background
                )

                {
                    if (mapState.isFollowing) {
                        Icon(
                            imageVector = Icons.Default.NearMe,
                            contentDescription = "Center View",
                            tint = MaterialTheme.colors.primary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.NearMe,
                            contentDescription = "Center View",
                            tint = MaterialTheme.colors.primary
                        )
                    }

                }
                FloatingActionButton(
                    onClick = {
                        locationOverlay?.myLocation?.let { myLocation ->
                            val longitude = myLocation.longitude
                            val latitude = myLocation.latitude
                            viewModel.onEvent(MapEvent.AddMarker(latitude, longitude))
                            navController.navigate(
                                Screen.AddEditMapEntryScreen.route +
                                        "?latitude=$latitude&" +
                                        "longitude=$longitude"
                            )


                        }
                    },


                    backgroundColor = MaterialTheme.colors.background
                )
                {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add map entry",
                        tint = MaterialTheme.colors.primary
                    )
                }


            }
        }
    }

}


fun createAndScaleIcon(context: Context, resource: Int, scale: Double): Drawable {
    val bitmap: Bitmap =
        BitmapFactory.decodeResource(
            context.resources,
            resource
        )

    val scaledBitmap = Bitmap.createScaledBitmap(
        bitmap,
        (bitmap.width * scale).toInt(),
        (bitmap.height * scale).toInt(),
        false
    )

    return BitmapDrawable(context.resources, scaledBitmap)
}