package com.example.rangerapp.feature_map.presentation.export


import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.NoPhotography
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.rangerapp.core.presentation.FilterEvent
import com.example.rangerapp.core.presentation.components.CustomAlertDialog
import com.example.rangerapp.core.presentation.components.MapEntryComparison
import com.example.rangerapp.core.presentation.components.RangeDateSelection
import com.example.rangerapp.feature_map.presentation.map_entries.FilterViewModel
import com.example.rangerapp.feature_map.presentation.map_entries.components.FilterSection
import com.example.rangerapp.feature_map.presentation.util.CustomFormat
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ExportScreen(
    navController: NavController,
    viewModel: ExportViewModel = hiltViewModel(),
    filterViewModel: FilterViewModel = hiltViewModel()
) {
    val startCreatedDate = filterViewModel.startCreatedDate.value
    val endCreatedDate = filterViewModel.endCreatedDate.value
    val startUpdatedDate = filterViewModel.startUpdatedDate.value
    val endUpdatedDate = filterViewModel.endUpdatedDate.value
    val startControlDate = filterViewModel.startControlDate.value
    val endControlDate = filterViewModel.endControlDate.value
    val searchBar = filterViewModel.searchBar.value
    val isFilterSectionVisible = filterViewModel.isFilterSectionVisible.value
    val deleteMessage = viewModel.deleteNotification.value
    val mapFilter = filterViewModel.mapFilter.value
    val context = LocalContext.current
    val isDialogOpen = viewModel.isDeleteDialogOpen.value
    val conflictPairs = viewModel.conflictPairs.value
    val scaffoldState = rememberScaffoldState()


    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                viewModel.onEvent(ExportEvent.ImportJson(uri))
            }
        })

    BackHandler() {
        if (isFilterSectionVisible) {
            filterViewModel.onEvent(FilterEvent.ToggleFilterView)
        } else navController.navigateUp()
    }


    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ExportViewModel.UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )

                }

                is ExportViewModel.UiEvent.ShareJson -> {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.putExtra(Intent.EXTRA_STREAM, event.uri)
                    intent.type = "application/json"
                    context.startActivity(intent)
                }

                is ExportViewModel.UiEvent.ShareCsv -> {
                    if (event.uriList.isNotEmpty()) {
                        val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, event.uriList)
                        intent.type = "text/csv"
                        context.startActivity(intent)
                    }


                }

            }
        }

    }

    Scaffold(scaffoldState = scaffoldState) { paddingValues ->

        CustomAlertDialog(
            onDismissRequest = { viewModel.onEvent(ExportEvent.DismissDeletion) },
            onConfirm = {
                viewModel.onEvent(
                    ExportEvent.ConfirmDeletion
                )
            },
            alertTitle = "Einträge löschen",
            alertContent = deleteMessage,
            isOpen = isDialogOpen
        )


        Column(
            modifier = Modifier
                .padding(paddingValues)
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
                        startDate = startCreatedDate?.let { CustomFormat.formatLocalDate(it) }
                            ?: "-",
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
                        onEndIconClick = {
                            filterViewModel.onEvent(
                                FilterEvent.ChangeCreatedEndDate(
                                    it
                                )
                            )
                        }
                    )

                    RangeDateSelection(
                        modifier = Modifier,
                        startDate = startUpdatedDate?.let { CustomFormat.formatLocalDate(it) }
                            ?: "-",
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
                        startDate = startControlDate?.let { CustomFormat.formatLocalDate(it) }
                            ?: "-",
                        startLabel = "Start Kontrolldatum",
                        onStartIconClick = {
                            filterViewModel.onEvent(
                                FilterEvent.ChangeControlStartDate(
                                    it
                                )
                            )
                        },
                        endDate = endControlDate?.let { CustomFormat.formatLocalDate(it) } ?: "-",
                        endDateLabel = "Ende KontrollDatum",
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
                            .fillMaxWidth()
                    )


                }

            }
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = {
                        viewModel.onEvent(
                            ExportEvent.ExportJson(
                                mapFilter,
                                startCreatedDate,
                                endCreatedDate,
                                startUpdatedDate,
                                endUpdatedDate,
                                startControlDate,
                                endControlDate,
                                searchBar,
                                true
                            )
                        )
                    }) {
                        Text(text = "JSON")
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = "Exportiere Json"
                        )
                    }
                    TextButton(onClick = {
                        viewModel.onEvent(
                            ExportEvent.ExportCsv(
                                mapFilter,
                                startCreatedDate,
                                endCreatedDate,
                                startUpdatedDate,
                                endUpdatedDate,
                                startControlDate,
                                endControlDate,
                                searchBar
                            )
                        )
                    }) {
                        Text(text = "CSV")
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = "Exportiere CSV"
                        )
                    }

                    TextButton(onClick = { filePickerLauncher.launch(arrayOf("application/json")) }
                    ) {
                        Text(text = "JSON")
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Importiere Json"
                        )
                    }

                    TextButton(onClick = {
                        viewModel.onEvent(
                            ExportEvent.RequestDeletion(
                                mapFilter,
                                startCreatedDate,
                                endCreatedDate,
                                startUpdatedDate,
                                endUpdatedDate,
                                startControlDate,
                                endControlDate,
                                searchBar
                            )
                        )
                    }) {
                        Text(text = "Löschen")
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = "Delete Item"
                        )

                    }

                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            filterViewModel.onEvent(FilterEvent.ToggleFilterView)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    TextButton(onClick = {
                        viewModel.onEvent(
                            ExportEvent.ExportJson(
                                mapFilter,
                                startCreatedDate,
                                endCreatedDate,
                                startUpdatedDate,
                                endUpdatedDate,
                                startControlDate,
                                endControlDate,
                                searchBar,
                                false
                            )
                        )
                    }) {
                        Text(text = "JSON")
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = "Exportiere Json ohne Bilder"
                        )
                        Icon(
                            imageVector = Icons.Default.NoPhotography,
                            contentDescription = "Exportiere Json ohne Bilder"
                        )
                    }
                }


                if (conflictPairs.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(
                                text = "Momentaner Eintrag",
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                            )

                        }
                        Divider(
                            color = MaterialTheme.colors.onBackground,
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(horizontal = 5.dp)
                                .width(1.dp)
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                text = "Importierter Eintrag",

                                )
                        }

                    }

                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {

                    items(conflictPairs) {
                        MapEntryComparison(
                            modifier = Modifier,
                            currentEntry = it.first,
                            importedEntry = it.second,
                            onSelectCurrentEntry = {
                                viewModel.onEvent(
                                    ExportEvent.SelectCurrentConflictEntry(
                                        it
                                    )
                                )
                            },
                            onSelectImportEntry = {
                                viewModel.onEvent(
                                    ExportEvent.SelectImportedConflictEntry(
                                        it
                                    )
                                )
                            }
                        )
                    }
                }


            }
        }
    }
}



