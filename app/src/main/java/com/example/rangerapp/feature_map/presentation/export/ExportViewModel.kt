package com.example.rangerapp.feature_map.presentation.export

import android.content.Intent
import android.net.Uri
import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.rangerapp.feature_map.data.repository.RangerAppFrontendException
import com.example.rangerapp.feature_map.domain.model.Biotop
import com.example.rangerapp.feature_map.domain.model.InvalidBiotopException
import com.example.rangerapp.feature_map.domain.model.InvalidMonitoringException
import com.example.rangerapp.feature_map.domain.model.InvalidOtherObservationException
import com.example.rangerapp.feature_map.domain.model.InvalidPlantControlException
import com.example.rangerapp.feature_map.domain.model.InvalidSpeciesReportException
import com.example.rangerapp.feature_map.domain.model.InvalidVisitorContactException
import com.example.rangerapp.feature_map.domain.model.MapEntry
import com.example.rangerapp.feature_map.domain.model.Monitoring
import com.example.rangerapp.feature_map.domain.model.OtherObservation
import com.example.rangerapp.feature_map.domain.model.PlantControl
import com.example.rangerapp.feature_map.domain.model.SpeciesReport
import com.example.rangerapp.feature_map.domain.model.VisitorContact
import com.example.rangerapp.feature_map.domain.use_case.biotop.BiotopUseCases
import com.example.rangerapp.feature_map.domain.use_case.file.FileStorageUseCases
import com.example.rangerapp.feature_map.domain.use_case.monitoring.MonitoringUseCases
import com.example.rangerapp.feature_map.domain.use_case.other_observation.OtherObservationUseCases
import com.example.rangerapp.feature_map.domain.use_case.plant_control.PlantControlUseCases
import com.example.rangerapp.feature_map.domain.use_case.species_report.SpeciesReportUseCases
import com.example.rangerapp.feature_map.domain.use_case.visitor_contact.VisitorContactUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val fileStorageUseCases: FileStorageUseCases,
    private val otherObservationUseCases: OtherObservationUseCases,
    private val plantControlUseCases: PlantControlUseCases,
    private val speciesReportUseCases: SpeciesReportUseCases,
    private val visitorContactUseCases: VisitorContactUseCases,
    private val monitoringUseCases: MonitoringUseCases,
    private val biotopUseCases: BiotopUseCases


) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()
    private val _isDeleteDialogOpen = mutableStateOf(false)
    val isDeleteDialogOpen: State<Boolean> = _isDeleteDialogOpen
    private val _conflictPairs = mutableStateOf(emptyList<Pair<MapEntry, MapEntry>>())
    val conflictPairs: State<List<Pair<MapEntry, MapEntry>>> = _conflictPairs
    private val _biotops = mutableStateOf<List<Biotop>>(emptyList())
    private val _monitorings = mutableStateOf<List<Monitoring>>(emptyList())
    private val _visitorContacts = mutableStateOf<List<VisitorContact>>(emptyList())
    private val _plantControls = mutableStateOf<List<PlantControl>>(emptyList())
    private val _speciesReports = mutableStateOf<List<SpeciesReport>>(emptyList())
    private val _otherObservations = mutableStateOf<List<OtherObservation>>(emptyList())
    private val _deleteNotification = mutableStateOf("")
    val deleteNotification: State<String> = _deleteNotification

    private val sharedContentState =
        savedStateHandle.getStateFlow(NavController.KEY_DEEP_LINK_INTENT, Intent())
            .let { intent ->
                val jsonFile: Uri? =
                    intent.value.getParcelableExtra(Intent.EXTRA_STREAM)
                if (jsonFile != null) {
                    onEvent(ExportEvent.ImportJson(jsonFile))
                }
            }


    fun onEvent(event: ExportEvent) {
        when (event) {
            is ExportEvent.ImportJson -> {
                var imported = 0
                var alreadyExist = 0
                var conflicts = 0

                viewModelScope.launch {
                    try {
                        val mapEntryList = fileStorageUseCases.importJson(event.uri)
                        for (entry in mapEntryList) {
                            when (entry.getMapEntryType()) {
                                MapEntry.MapEntryType.SPECIES_REPORT -> {
                                    val newEntry = entry as SpeciesReport
                                    try {
                                        val result: Long =
                                            speciesReportUseCases.addIgnoreSpeciesReport(newEntry)
                                        if (result == -1L) {
                                            speciesReportUseCases.getSpeciesReport(
                                                createdTimestamp = entry.createdTimestamp,
                                                latitude = entry.latitude,
                                                longitude = entry.longitude
                                            )?.let { currentEntry ->
                                                if (!newEntry.importEquals(currentEntry)) {
                                                    _conflictPairs.value =
                                                        conflictPairs.value + Pair(
                                                            currentEntry,
                                                            newEntry
                                                        )
                                                    conflicts += 1
                                                } else {
                                                    alreadyExist += 1
                                                }
                                            }
                                        } else {
                                            imported += 1
                                        }
                                    } catch (e: InvalidSpeciesReportException) {
                                        throw RangerAppFrontendException("Der Eintrag mit dem Zeitstempel ${entry.createdTimestamp} konnte aus folgendem Grund nicht importiert werden:\n\"${e.message.toString()}\"")
                                    }
                                }

                                MapEntry.MapEntryType.VISITOR_CONTACT -> {
                                    val newEntry = entry as VisitorContact
                                    try {
                                        val result: Long =
                                            visitorContactUseCases.addIgnoreVisitorContact(newEntry)
                                        if (result == -1L) {
                                            visitorContactUseCases.getVisitorContact(
                                                createdTimestamp = entry.createdTimestamp,
                                                latitude = entry.latitude,
                                                longitude = entry.longitude
                                            )?.let { currentEntry ->
                                                if (!newEntry.importEquals(currentEntry)) {
                                                    _conflictPairs.value =
                                                        conflictPairs.value + Pair(
                                                            currentEntry,
                                                            newEntry
                                                        )
                                                    conflicts += 1
                                                } else {
                                                    alreadyExist += 1
                                                }
                                            }
                                        } else {
                                            imported += 1
                                        }
                                    } catch (e: InvalidVisitorContactException) {
                                        throw RangerAppFrontendException("Der Eintrag mit dem Zeitstempel ${entry.createdTimestamp} konnte aus folgendem Grund nicht importiert werden:\n\"${e.message.toString()}\"")
                                    }
                                }

                                MapEntry.MapEntryType.PLANT_CONTROL -> {
                                    val newEntry = entry as PlantControl
                                    try {
                                        val result: Long =
                                            plantControlUseCases.addIgnorePlantControl(newEntry)
                                        if (result == -1L) {
                                            plantControlUseCases.getPlantControl(
                                                createdTimestamp = entry.createdTimestamp,
                                                latitude = entry.latitude,
                                                longitude = entry.longitude
                                            )?.let { currentEntry ->
                                                if (!newEntry.importEquals(currentEntry)) {
                                                    _conflictPairs.value =
                                                        conflictPairs.value + Pair(
                                                            currentEntry,
                                                            newEntry
                                                        )
                                                    conflicts += 1
                                                } else {
                                                    alreadyExist += 1
                                                }
                                            }
                                        } else {
                                            imported += 1
                                        }
                                    } catch (e: InvalidPlantControlException) {
                                        throw RangerAppFrontendException("Der Eintrag mit dem Zeitstempel ${entry.createdTimestamp} konnte aus folgendem Grund nicht importiert werden:\n\"${e.message.toString()}\"")

                                    }
                                }

                                MapEntry.MapEntryType.OTHER_OBSERVATION -> {
                                    val newEntry = entry as OtherObservation
                                    try {
                                        val result: Long =
                                            otherObservationUseCases.addIgnoreOtherObservation(
                                                newEntry
                                            )
                                        if (result == -1L) {
                                            otherObservationUseCases.getOtherObservation(
                                                createdTimestamp = entry.createdTimestamp,
                                                latitude = entry.latitude,
                                                longitude = entry.longitude
                                            )?.let { currentEntry ->
                                                if (!newEntry.importEquals(currentEntry)) {
                                                    _conflictPairs.value =
                                                        conflictPairs.value + Pair(
                                                            currentEntry,
                                                            newEntry
                                                        )
                                                    conflicts += 1
                                                } else {
                                                    alreadyExist += 1
                                                }
                                            }
                                        } else {
                                            imported += 1
                                        }
                                    } catch (e: InvalidOtherObservationException) {
                                        throw RangerAppFrontendException("Der Eintrag mit dem Zeitstempel ${entry.createdTimestamp} konnte aus folgendem Grund nicht importiert werden:\n\"${e.message.toString()}\"")
                                    }
                                }

                                MapEntry.MapEntryType.MONITORING -> {
                                    val newEntry = entry as Monitoring
                                    try {
                                        val result: Long =
                                            monitoringUseCases.addIgnoreMonitoring(newEntry)
                                        if (result == -1L) {
                                            monitoringUseCases.getMonitoring(
                                                createdTimestamp = entry.createdTimestamp,
                                                latitude = entry.latitude,
                                                longitude = entry.longitude
                                            )?.let { currentEntry ->
                                                if (!newEntry.importEquals(currentEntry)) {
                                                    _conflictPairs.value =
                                                        conflictPairs.value + Pair(
                                                            currentEntry,
                                                            newEntry
                                                        )
                                                    conflicts += 1
                                                } else {
                                                    alreadyExist += 1
                                                }
                                            }
                                        } else {
                                            imported += 1
                                        }
                                    } catch (e: InvalidMonitoringException) {
                                        throw RangerAppFrontendException("Der Eintrag mit dem Zeitstempel ${entry.createdTimestamp} konnte aus folgendem Grund nicht importiert werden:\n\"${e.message.toString()}\"")
                                    }
                                }

                                MapEntry.MapEntryType.BIOTOP -> {
                                    val newEntry = entry as Biotop
                                    try {
                                        val result: Long =
                                            biotopUseCases.addIgnoreBiotop(newEntry)
                                        if (result == -1L) {
                                            biotopUseCases.getBiotop(
                                                createdTimestamp = entry.createdTimestamp,
                                                latitude = entry.latitude,
                                                longitude = entry.longitude
                                            )?.let { currentEntry ->
                                                if (!newEntry.importEquals(currentEntry)) {
                                                    _conflictPairs.value =
                                                        conflictPairs.value + Pair(
                                                            currentEntry,
                                                            newEntry
                                                        )
                                                    conflicts += 1
                                                } else {
                                                    alreadyExist += 1
                                                }
                                            }
                                        } else {
                                            imported += 1
                                        }
                                    } catch (e: InvalidBiotopException) {
                                        throw RangerAppFrontendException("Der Eintrag mit dem Zeitstempel ${entry.createdTimestamp} konnte aus folgendem Grund nicht importiert werden:\n\"${e.message.toString()}\"")
                                    }
                                }
                            }
                        }
                        val message = "$imported neue Einträge importiert. ${
                            if (alreadyExist > 0) {
                                "$alreadyExist Einträge existierten bereits."
                            } else ""
                        } ${
                            if (conflicts > 0) {
                                "$conflicts Konflikt(e) erkannt"
                            } else ""
                        }"
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(
                                message, SnackbarDuration.Long
                            )
                        )

                    } catch (e: RangerAppFrontendException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(
                                message = e.message.toString(),
                                duration = SnackbarDuration.Long
                            )
                        )
                    }
                }
            }

            is ExportEvent.ExportCsv -> {
                viewModelScope.launch {
                    val speciesReportsDeferred = viewModelScope.async(Dispatchers.IO) {
                        if (event.mapEntryTypes.contains(MapEntry.MapEntryType.SPECIES_REPORT) && event.endControlDate == null && event.startControlDate == null) {
                            speciesReportUseCases.getSpeciesReports(
                                endCreatedDate = event.endCreatedDate,
                                startCreatedDate = event.startCreatedDate,
                                endUpdatedDate = event.endUpdatedDate,
                                startUpdatedDate = event.startUpdatedDate,
                                searchTerm = event.searchTerm
                            )
                        } else {
                            emptyList()
                        }.let {
                            if (it.isNotEmpty()) {
                                fileStorageUseCases.exportCsv(
                                    it,
                                    MapEntry.MapEntryType.SPECIES_REPORT
                                )
                            } else {
                                null
                            }
                        }
                    }
                    val otherObservationsDeferred = viewModelScope.async(Dispatchers.IO) {
                        if (event.mapEntryTypes.contains(MapEntry.MapEntryType.OTHER_OBSERVATION) && event.endControlDate == null && event.startControlDate == null) {
                            otherObservationUseCases.getOtherObservations(
                                endCreatedDate = event.endCreatedDate,
                                startCreatedDate = event.startCreatedDate,
                                endUpdatedDate = event.endUpdatedDate,
                                startUpdatedDate = event.startUpdatedDate,
                                searchTerm = event.searchTerm
                            )
                        } else {
                            emptyList()
                        }.let {
                            if (it.isNotEmpty()) {

                                fileStorageUseCases.exportCsv(
                                    it,
                                    MapEntry.MapEntryType.OTHER_OBSERVATION
                                )
                            } else {
                                null
                            }
                        }
                    }
                    val plantControlsDeferred = viewModelScope.async(Dispatchers.IO) {
                        if (event.mapEntryTypes.contains(MapEntry.MapEntryType.PLANT_CONTROL) && event.endControlDate == null && event.startControlDate == null) {
                            plantControlUseCases.getPlantControls(
                                endCreatedDate = event.endCreatedDate,
                                startCreatedDate = event.startCreatedDate,
                                endUpdatedDate = event.endUpdatedDate,
                                startUpdatedDate = event.startUpdatedDate,
                                searchTerm = event.searchTerm
                            )
                        } else {
                            emptyList()
                        }.let {
                            if (it.isNotEmpty()) {

                                fileStorageUseCases.exportCsv(
                                    it,
                                    MapEntry.MapEntryType.PLANT_CONTROL
                                )
                            } else {
                                null
                            }
                        }
                    }
                    val visitorContactsDeferred = viewModelScope.async(Dispatchers.IO) {
                        if (event.mapEntryTypes.contains(MapEntry.MapEntryType.VISITOR_CONTACT) && event.endControlDate == null && event.startControlDate == null) {
                            visitorContactUseCases.getVisitorContacts(
                                endCreatedDate = event.endCreatedDate,
                                startCreatedDate = event.startCreatedDate,
                                endUpdatedDate = event.endUpdatedDate,
                                startUpdatedDate = event.startUpdatedDate,
                                searchTerm = event.searchTerm
                            )
                        } else {
                            emptyList()
                        }.let {
                            if (it.isNotEmpty()) {
                                fileStorageUseCases.exportCsv(
                                    it,
                                    MapEntry.MapEntryType.VISITOR_CONTACT
                                )
                            } else {
                                null
                            }
                        }
                    }

                    val monitoringDeferred = viewModelScope.async(Dispatchers.IO) {
                        if (event.mapEntryTypes.contains(MapEntry.MapEntryType.MONITORING)) {
                            monitoringUseCases.getMonitorings(
                                endCreatedDate = event.endCreatedDate,
                                startCreatedDate = event.startCreatedDate,
                                endUpdatedDate = event.endUpdatedDate,
                                startUpdatedDate = event.startUpdatedDate,
                                startControlDate = event.startControlDate,
                                endControlDate = event.endControlDate,
                                searchTerm = event.searchTerm
                            )
                        } else {
                            emptyList()
                        }.let {
                            if (it.isNotEmpty()) {

                                fileStorageUseCases.exportCsv(
                                    it,
                                    MapEntry.MapEntryType.MONITORING
                                )
                            } else {
                                null
                            }
                        }
                    }

                    val biotopDeferred = viewModelScope.async(Dispatchers.IO) {
                        if (event.mapEntryTypes.contains(MapEntry.MapEntryType.BIOTOP) && event.endControlDate == null && event.startControlDate == null) {
                            biotopUseCases.getBiotops(
                                endCreatedDate = event.endCreatedDate,
                                startCreatedDate = event.startCreatedDate,
                                endUpdatedDate = event.endUpdatedDate,
                                startUpdatedDate = event.startUpdatedDate,
                                searchTerm = event.searchTerm,
                            )
                        } else {
                            emptyList()
                        }.let {
                            if (it.isNotEmpty()) {
                                fileStorageUseCases.exportCsv(
                                    it,
                                    MapEntry.MapEntryType.BIOTOP
                                )
                            } else {
                                null
                            }
                        }
                    }


                    val uriList = ArrayList<Uri>()
                    speciesReportsDeferred.await()?.let { uriList.add(it) }
                    otherObservationsDeferred.await()?.let { uriList.add(it) }
                    visitorContactsDeferred.await()?.let { uriList.add(it) }
                    plantControlsDeferred.await()?.let { uriList.add(it) }
                    monitoringDeferred.await()?.let { uriList.add(it) }
                    biotopDeferred.await()?.let { uriList.add(it) }



                    if (uriList.isEmpty()) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(
                                message = "Keine Einträge zum Exportieren gefunden",
                                duration = SnackbarDuration.Short
                            )
                        )
                    } else {
                        _eventFlow.emit(UiEvent.ShareCsv(uriList))

                    }

                }
            }

            is ExportEvent.ExportJson -> {
                val mapEntries = mutableListOf<MapEntry>()
                viewModelScope.launch {
                    val speciesReportsDeferred = viewModelScope.async(Dispatchers.IO) {
                        if (event.mapEntryTypes.contains(MapEntry.MapEntryType.SPECIES_REPORT) && event.endControlDate == null && event.startControlDate == null) {
                            speciesReportUseCases.getSpeciesReports(
                                endCreatedDate = event.endCreatedDate,
                                startCreatedDate = event.startCreatedDate,
                                endUpdatedDate = event.endUpdatedDate,
                                startUpdatedDate = event.startUpdatedDate,
                                searchTerm = event.searchTerm
                            )
                        } else {
                            emptyList()
                        }
                    }
                    val otherObservationsDeferred = viewModelScope.async(Dispatchers.IO) {
                        if (event.mapEntryTypes.contains(MapEntry.MapEntryType.OTHER_OBSERVATION) && event.endControlDate == null && event.startControlDate == null) {
                            otherObservationUseCases.getOtherObservations(
                                endCreatedDate = event.endCreatedDate,
                                startCreatedDate = event.startCreatedDate,
                                endUpdatedDate = event.endUpdatedDate,
                                startUpdatedDate = event.startUpdatedDate,
                                searchTerm = event.searchTerm
                            )
                        } else {
                            emptyList()
                        }
                    }
                    val plantControlsDeferred = viewModelScope.async(Dispatchers.IO) {
                        if (event.mapEntryTypes.contains(MapEntry.MapEntryType.PLANT_CONTROL) && event.endControlDate == null && event.startControlDate == null) {
                            plantControlUseCases.getPlantControls(
                                endCreatedDate = event.endCreatedDate,
                                startCreatedDate = event.startCreatedDate,
                                endUpdatedDate = event.endUpdatedDate,
                                startUpdatedDate = event.startUpdatedDate,
                                searchTerm = event.searchTerm
                            )
                        } else {
                            emptyList()
                        }
                    }
                    val visitorContactsDeferred = viewModelScope.async(Dispatchers.IO) {
                        if (event.mapEntryTypes.contains(MapEntry.MapEntryType.VISITOR_CONTACT) && event.endControlDate == null && event.startControlDate == null) {
                            visitorContactUseCases.getVisitorContacts(
                                endCreatedDate = event.endCreatedDate,
                                startCreatedDate = event.startCreatedDate,
                                endUpdatedDate = event.endUpdatedDate,
                                startUpdatedDate = event.startUpdatedDate,
                                searchTerm = event.searchTerm
                            )
                        } else {
                            emptyList()
                        }
                    }
                    val monitoringDeferred = viewModelScope.async(Dispatchers.IO) {
                        if (event.mapEntryTypes.contains(MapEntry.MapEntryType.MONITORING)) {
                            monitoringUseCases.getMonitorings(
                                endCreatedDate = event.endCreatedDate,
                                startCreatedDate = event.startCreatedDate,
                                endUpdatedDate = event.endUpdatedDate,
                                startUpdatedDate = event.startUpdatedDate,
                                startControlDate = event.startControlDate,
                                endControlDate = event.endControlDate,
                                searchTerm = event.searchTerm
                            )
                        } else {
                            emptyList()
                        }
                    }

                    val biotopDeferred = viewModelScope.async(Dispatchers.IO) {
                        if (event.mapEntryTypes.contains(MapEntry.MapEntryType.BIOTOP) && event.endControlDate == null && event.startControlDate == null) {
                            biotopUseCases.getBiotops(
                                endCreatedDate = event.endCreatedDate,
                                startCreatedDate = event.startCreatedDate,
                                endUpdatedDate = event.endUpdatedDate,
                                startUpdatedDate = event.startUpdatedDate,
                                searchTerm = event.searchTerm
                            )
                        } else {
                            emptyList()
                        }
                    }


                    val speciesReports = speciesReportsDeferred.await()
                    val otherObservations = otherObservationsDeferred.await()
                    val visitorContacts = visitorContactsDeferred.await()
                    val plantControls = plantControlsDeferred.await()
                    val monitorings = monitoringDeferred.await()
                    val biotops = biotopDeferred.await()

                    mapEntries.addAll(speciesReports)
                    mapEntries.addAll(otherObservations)
                    mapEntries.addAll(visitorContacts)
                    mapEntries.addAll(plantControls)
                    mapEntries.addAll(monitorings)
                    mapEntries.addAll(biotops)

                    if (mapEntries.isNotEmpty()) {
                        val uri =
                            fileStorageUseCases.exportJson.invoke(mapEntries, event.withPictures)
                        uri?.let {
                            _eventFlow.emit(UiEvent.ShareJson(it))
                        }
                    } else {
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(
                                message = "Keine Einträge zum Exportieren gefunden",
                                duration = SnackbarDuration.Short
                            )
                        )
                    }
                }
            }

            is ExportEvent.RequestDeletion -> {
                var speciesReportsSize = 0
                var plantControlSize = 0
                var otherObservationSize = 0
                var visitorContactSize = 0
                var monitoringSize = 0
                var biotopSize = 0
                viewModelScope.launch {
                    val speciesReportsDeferred = viewModelScope.async(Dispatchers.IO) {
                        if (event.mapEntryTypes.contains(MapEntry.MapEntryType.SPECIES_REPORT) && event.endControlDate == null && event.startControlDate == null) {
                            speciesReportUseCases.getSpeciesReports(
                                endCreatedDate = event.endCreatedDate,
                                startCreatedDate = event.startCreatedDate,
                                endUpdatedDate = event.endUpdatedDate,
                                startUpdatedDate = event.startUpdatedDate,
                                searchTerm = event.searchTerm
                            ).also {
                                speciesReportsSize = it.size
                                _speciesReports.value = it
                            }
                        }
                    }
                    val otherObservationsDeferred = viewModelScope.async(Dispatchers.IO) {
                        if (event.mapEntryTypes.contains(MapEntry.MapEntryType.OTHER_OBSERVATION) && event.endControlDate == null && event.startControlDate == null) {
                            otherObservationUseCases.getOtherObservations(
                                endCreatedDate = event.endCreatedDate,
                                startCreatedDate = event.startCreatedDate,
                                endUpdatedDate = event.endUpdatedDate,
                                startUpdatedDate = event.startUpdatedDate,
                                searchTerm = event.searchTerm
                            ).also {
                                otherObservationSize = it.size
                                _otherObservations.value = it
                            }
                        }
                    }
                    val plantControlsDeferred = viewModelScope.async(Dispatchers.IO) {
                        if (event.mapEntryTypes.contains(MapEntry.MapEntryType.PLANT_CONTROL) && event.endControlDate == null && event.startControlDate == null) {
                            plantControlUseCases.getPlantControls(
                                endCreatedDate = event.endCreatedDate,
                                startCreatedDate = event.startCreatedDate,
                                endUpdatedDate = event.endUpdatedDate,
                                startUpdatedDate = event.startUpdatedDate,
                                searchTerm = event.searchTerm
                            ).also {
                                plantControlSize = it.size
                                _plantControls.value = it
                            }
                        }
                    }
                    val visitorContactsDeferred = viewModelScope.async(Dispatchers.IO) {
                        if (event.mapEntryTypes.contains(MapEntry.MapEntryType.VISITOR_CONTACT) && event.endControlDate == null && event.startControlDate == null) {
                            visitorContactUseCases.getVisitorContacts(
                                endCreatedDate = event.endCreatedDate,
                                startCreatedDate = event.startCreatedDate,
                                endUpdatedDate = event.endUpdatedDate,
                                startUpdatedDate = event.startUpdatedDate,
                                searchTerm = event.searchTerm
                            ).also {
                                visitorContactSize = it.size
                                _visitorContacts.value = it
                            }
                        }
                    }

                    val monitoringsDeferred = viewModelScope.async(Dispatchers.IO) {
                        if (event.mapEntryTypes.contains(MapEntry.MapEntryType.MONITORING)) {
                            monitoringUseCases.getMonitorings(
                                endCreatedDate = event.endCreatedDate,
                                startCreatedDate = event.startCreatedDate,
                                endUpdatedDate = event.endUpdatedDate,
                                startUpdatedDate = event.startUpdatedDate,
                                startControlDate = event.startControlDate,
                                endControlDate = event.endControlDate,
                                searchTerm = event.searchTerm
                            ).also {
                                monitoringSize = it.size
                                _monitorings.value = it

                            }
                        }
                    }

                    val biotopsDeferred = viewModelScope.async(Dispatchers.IO) {
                        if (event.mapEntryTypes.contains(MapEntry.MapEntryType.BIOTOP) && event.endControlDate == null && event.startControlDate == null) {
                            biotopUseCases.getBiotops(
                                endCreatedDate = event.endCreatedDate,
                                startCreatedDate = event.startCreatedDate,
                                endUpdatedDate = event.endUpdatedDate,
                                startUpdatedDate = event.startUpdatedDate,
                                searchTerm = event.searchTerm
                            ).also {
                                biotopSize = it.size
                                _biotops.value = it
                            }
                        }
                    }


                    speciesReportsDeferred.await()
                    otherObservationsDeferred.await()
                    visitorContactsDeferred.await()
                    plantControlsDeferred.await()
                    monitoringsDeferred.await()
                    biotopsDeferred.await()


                    val mapEntriesSize =
                        speciesReportsSize + otherObservationSize + visitorContactSize + plantControlSize + biotopSize + monitoringSize
                    val message =
                        "Sollen die folgenden $mapEntriesSize Einträge gelöscht werden?\n" +
                                "${
                                    if (speciesReportsSize > 0) {
                                        "Arten Meldungen: $speciesReportsSize\n"
                                    } else ""
                                }${
                                    if (visitorContactSize > 0) {
                                        "Besucherkontakte: $visitorContactSize\n"
                                    } else ""
                                }${
                                    if (plantControlSize > 0) {
                                        "AnlagenKontrollen: $plantControlSize\n"
                                    } else ""
                                }${
                                    if (otherObservationSize > 0) {
                                        "Sonstige Beobachtungen: $otherObservationSize\n"
                                    } else ""
                                }${
                                    if (biotopSize > 0) {
                                        "Biotope: $biotopSize\n"
                                    } else ""
                                }${
                                    if (monitoringSize > 0) {
                                        "Monitorings: $monitoringSize\n"
                                    } else ""
                                }"

                    if (mapEntriesSize == 0) {
                        _eventFlow.emit(UiEvent.ShowSnackbar(message = "Keine Einträge zum Löschen gefunden"))
                    } else {
                        _deleteNotification.value = message
                        _isDeleteDialogOpen.value = true
                    }
                }

            }

            ExportEvent.DismissDeletion -> {
                resetDeleteEntries()
                _deleteNotification.value = ""
                _isDeleteDialogOpen.value = false
            }

            is ExportEvent.SelectCurrentConflictEntry -> {
                _conflictPairs.value = conflictPairs.value - event.mapEntryPair
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ShowSnackbar(message = "Eintrag wurde ignoriert"))

                }

            }

            is ExportEvent.SelectImportedConflictEntry -> {
                viewModelScope.launch {
                    when (event.mapEntryPair.second.getMapEntryType()) {
                        MapEntry.MapEntryType.SPECIES_REPORT -> {
                            val updateSpeciesReport = event.mapEntryPair.second as SpeciesReport
                            speciesReportUseCases.addSpeciesReport(updateSpeciesReport)
                        }

                        MapEntry.MapEntryType.VISITOR_CONTACT -> {
                            val updatedVisitorContact = event.mapEntryPair.second as VisitorContact
                            visitorContactUseCases.addVisitorContact(updatedVisitorContact)
                        }

                        MapEntry.MapEntryType.PLANT_CONTROL -> {
                            val updatedPlantControl = event.mapEntryPair.second as PlantControl
                            plantControlUseCases.addPlantControl(updatedPlantControl)

                        }

                        MapEntry.MapEntryType.OTHER_OBSERVATION -> {
                            val updatedOtherObservation =
                                event.mapEntryPair.second as OtherObservation
                            otherObservationUseCases.addOtherObservation(updatedOtherObservation)
                        }

                        MapEntry.MapEntryType.MONITORING -> {
                            val updatedMonitoring =
                                event.mapEntryPair.second as Monitoring
                            monitoringUseCases.addMonitoring(updatedMonitoring)
                        }

                        MapEntry.MapEntryType.BIOTOP -> {
                            val updatedBiotop =
                                event.mapEntryPair.second as Biotop
                            biotopUseCases.addBiotop(updatedBiotop)
                        }
                    }
                    _eventFlow.emit(UiEvent.ShowSnackbar(message = "Eintrag wurde aktualisiert"))

                }
                _conflictPairs.value = conflictPairs.value - event.mapEntryPair

            }

            ExportEvent.ConfirmDeletion -> {

                viewModelScope.launch {

                    val speciesReportsDeferred = viewModelScope.async(Dispatchers.IO) {
                        speciesReportUseCases.deleteSpeciesReport(_speciesReports.value)
                    }
                    val otherObservationsDeferred = viewModelScope.async(Dispatchers.IO) {
                        otherObservationUseCases.deleteOtherObservations(_otherObservations.value)
                    }
                    val visitorContactsDeferred = viewModelScope.async(Dispatchers.IO) {
                        visitorContactUseCases.deleteVisitorContact(_visitorContacts.value)
                    }
                    val plantControlsDeferred = viewModelScope.async(Dispatchers.IO) {
                        plantControlUseCases.deletePlantControl(_plantControls.value)
                    }

                    val monitoringsDeferred = viewModelScope.async(Dispatchers.IO) {
                        monitoringUseCases.deleteMonitoring(_monitorings.value)
                    }
                    val biotopsDeferred = viewModelScope.async(Dispatchers.IO) {
                        biotopUseCases.deleteBiotops(_biotops.value)
                    }

                    speciesReportsDeferred.await()
                    otherObservationsDeferred.await()
                    visitorContactsDeferred.await()
                    plantControlsDeferred.await()
                    monitoringsDeferred.await()
                    biotopsDeferred.await()
                    _eventFlow.emit(UiEvent.ShowSnackbar(message = "Einträge wurden gelöscht"))
                }
                _isDeleteDialogOpen.value = false

            }
        }
    }

    private fun resetDeleteEntries() {
        _biotops.value = emptyList()
        _monitorings.value = emptyList()
        _visitorContacts.value = emptyList()
        _plantControls.value = emptyList()
        _speciesReports.value = emptyList()
        _otherObservations.value = emptyList()
    }

    sealed class UiEvent {
        data class ShowSnackbar(
            val message: String,
            val duration: SnackbarDuration = SnackbarDuration.Short
        ) : UiEvent()


        data class ShareJson(val uri: Uri) : UiEvent()
        data class ShareCsv(val uriList: ArrayList<Uri>) : UiEvent()
    }

}