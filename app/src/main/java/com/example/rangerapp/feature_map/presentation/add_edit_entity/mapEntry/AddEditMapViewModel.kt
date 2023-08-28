package com.example.rangerapp.feature_map.presentation.add_edit_entity.mapEntry

import android.content.IntentSender
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rangerapp.feature_map.domain.model.Biotop
import com.example.rangerapp.feature_map.domain.model.BiotopCategory
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
import com.example.rangerapp.feature_map.domain.model.SpeciesCategory
import com.example.rangerapp.feature_map.domain.model.SpeciesReport
import com.example.rangerapp.feature_map.domain.model.VisitorContact
import com.example.rangerapp.feature_map.domain.model.getBiotopCategoryByName
import com.example.rangerapp.feature_map.domain.model.getSpeciesCategoryByName
import com.example.rangerapp.feature_map.domain.use_case.biotop.BiotopUseCases
import com.example.rangerapp.feature_map.domain.use_case.file.FileStorageUseCases
import com.example.rangerapp.feature_map.domain.use_case.monitoring.MonitoringUseCases
import com.example.rangerapp.feature_map.domain.use_case.other_observation.OtherObservationUseCases
import com.example.rangerapp.feature_map.domain.use_case.plant_control.PlantControlUseCases
import com.example.rangerapp.feature_map.domain.use_case.species_report.SpeciesReportUseCases
import com.example.rangerapp.feature_map.domain.use_case.visitor_contact.VisitorContactUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class AddEditMapViewModel @Inject constructor(
    private val otherObservationUseCases: OtherObservationUseCases,
    private val plantControlUseCases: PlantControlUseCases,
    private val speciesReportUseCases: SpeciesReportUseCases,
    private val visitorContactUseCases: VisitorContactUseCases,
    private val fileStorageUseCases: FileStorageUseCases,
    private val biotopUseCases: BiotopUseCases,
    private val monitoringUseCases: MonitoringUseCases,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _mapEntryDescription = mutableStateOf("")
    val mapEntryDescription: State<String> = _mapEntryDescription

    private val _mapEntryBiotopType = mutableStateOf("")
    val mapEntryBiotopType: State<String> = _mapEntryBiotopType

    private val _mapEntryResult = mutableStateOf("")
    val mapEntryResult: State<String> = _mapEntryResult

    private val _mapEntryMonitoringType = mutableStateOf("")
    val mapEntryMonitoringType: State<String> = _mapEntryMonitoringType

    private val _mapEntryMaterial = mutableStateOf("")
    val mapEntryMaterial: State<String> = _mapEntryMaterial

    private val _mapEntryType = mutableStateOf(MapEntry.MapEntryType.SPECIES_REPORT)
    val mapEntryType: State<MapEntry.MapEntryType> = _mapEntryType

    private val _mapEntrySpeciesContent = mutableStateOf("")
    val mapEntrySpeciesContent: State<String> = _mapEntrySpeciesContent

    private val _mapEntryVisitorOrigin = mutableStateOf("")
    val mapEntryVisitorOrigin: State<String> = _mapEntryVisitorOrigin

    private val _mapEntryVisitorViolations = mutableStateOf("")
    val mapEntryVisitorViolations: State<String> = _mapEntryVisitorViolations

    private val _mapEntrySpeciesCategory = mutableStateOf(SpeciesCategory.VOGEL.value)
    val mapEntrySpeciesCategory: State<String> = _mapEntrySpeciesCategory

    private val _mapEntryBiotopCategory = mutableStateOf(BiotopCategory.WALD.value)
    val mapEntryBiotopCategory: State<String> = _mapEntryBiotopCategory

    private val _isBiotopCategoryExpanded = mutableStateOf(false)
    val isBiotopCategoryExpanded: State<Boolean> = _isBiotopCategoryExpanded

    private val _textFiledSize = mutableStateOf(Size.Zero)
    val textFiledSize: State<Size> = _textFiledSize

    private val _isMapEntryCategoryExpanded = mutableStateOf(false)
    val isMapEntryCategoryExpanded: State<Boolean> = _isMapEntryCategoryExpanded

    private val _mapEntryQuantity = mutableStateOf("")
    val mapEntryQuantity: State<String> = _mapEntryQuantity

    private val _mapEntryLongitude = mutableStateOf(0.0)
    val mapEntryLongitude: State<Double> = _mapEntryLongitude

    private val _mapEntryLatitude = mutableStateOf(0.0)
    val mapEntryLatitude: State<Double> = _mapEntryLatitude

    private val _mapEntryCreated =
        mutableStateOf(LocalDateTime.ofEpochSecond(0L, 0, ZoneOffset.UTC))
    val mapEntryCreated: State<LocalDateTime> = _mapEntryCreated

    private val _mapEntryUpdated =
        mutableStateOf(LocalDateTime.ofEpochSecond(0L, 0, ZoneOffset.UTC))
    val mapEntryUpdated: State<LocalDateTime> = _mapEntryUpdated

    private val _mapEntryImageUris = mutableStateOf<Set<Uri>>(emptySet())
    val mapEntryImageUris: State<Set<Uri>> = _mapEntryImageUris

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _oldMapEntry = mutableStateOf<MapEntry?>(null)
    val oldMapEntry: State<MapEntry?> = _oldMapEntry

    private val _mapEntryControlDate = mutableStateOf<LocalDate?>(null)
    val mapEntryControlDate: State<LocalDate?> = _mapEntryControlDate

    private val _isDeleteDialogOpen = mutableStateOf(false)
    val isDeleteDialogOpen: State<Boolean> = _isDeleteDialogOpen

    private var currentMapEntryId: Int? = null


    init {
        savedStateHandle.get<Int>("mapEntryId")?.let { mapEntryId ->
            savedStateHandle.get<String>("mapEntryType")?.let { text ->
                val mapEntryType = MapEntry.MapEntryType.valueOf(text)
                if (mapEntryId != -1) {
                    viewModelScope.launch {
                        when (mapEntryType) {
                            MapEntry.MapEntryType.SPECIES_REPORT -> speciesReportUseCases.getSpeciesReport(
                                mapEntryId
                            )?.also { report ->
                                currentMapEntryId = mapEntryId
                                _mapEntryType.value = mapEntryType
                                _mapEntryDescription.value = report.description ?: ""
                                _mapEntryCreated.value = report.createdTimestamp
                                _mapEntryUpdated.value = report.lastUpdatedTimestamp
                                _mapEntryLatitude.value = report.latitude
                                _mapEntryLongitude.value = report.longitude
                                _mapEntrySpeciesContent.value = report.species
                                _mapEntryQuantity.value = report.quantity.toString()
                                _mapEntrySpeciesCategory.value = report.category.value
                                _mapEntryImageUris.value = report.imageUris.toSet()
                                _oldMapEntry.value = report.copy()
                            }

                            MapEntry.MapEntryType.VISITOR_CONTACT -> visitorContactUseCases.getVisitorContact(
                                mapEntryId
                            )?.also { report ->
                                currentMapEntryId = mapEntryId
                                _mapEntryType.value = mapEntryType
                                _mapEntryCreated.value = report.createdTimestamp
                                _mapEntryUpdated.value = report.lastUpdatedTimestamp
                                _mapEntryDescription.value = report.description ?: ""
                                _mapEntryLatitude.value = report.latitude
                                _mapEntryLongitude.value = report.longitude
                                _mapEntryQuantity.value = report.quantity.toString()
                                _mapEntryVisitorViolations.value = report.violations ?: ""
                                _mapEntryVisitorOrigin.value = report.origin ?: ""
                                _oldMapEntry.value = report.copy()

                            }

                            MapEntry.MapEntryType.PLANT_CONTROL -> plantControlUseCases.getPlantControl(
                                mapEntryId
                            )?.also { report ->
                                currentMapEntryId = mapEntryId
                                _mapEntryType.value = mapEntryType
                                _mapEntryCreated.value = report.createdTimestamp
                                _mapEntryUpdated.value = report.lastUpdatedTimestamp
                                _mapEntryDescription.value = report.description ?: ""
                                _mapEntryLatitude.value = report.latitude
                                _mapEntryLongitude.value = report.longitude
                                _mapEntryImageUris.value = report.imageUris.toSet()
                                _oldMapEntry.value = report.copy()

                            }

                            MapEntry.MapEntryType.OTHER_OBSERVATION -> otherObservationUseCases.getOtherObservation(
                                mapEntryId
                            )?.also { report ->
                                currentMapEntryId = mapEntryId
                                _mapEntryType.value = mapEntryType
                                _mapEntryCreated.value = report.createdTimestamp
                                _mapEntryUpdated.value = report.lastUpdatedTimestamp
                                _mapEntryDescription.value = report.description ?: ""
                                _mapEntryLatitude.value = report.latitude
                                _mapEntryLongitude.value = report.longitude
                                _mapEntryImageUris.value = report.imageUris.toSet()
                                _oldMapEntry.value = report.copy()

                            }

                            MapEntry.MapEntryType.MONITORING -> {
                                monitoringUseCases.getMonitoring(mapEntryId)?.also { report ->
                                    currentMapEntryId = mapEntryId
                                    _mapEntryType.value = mapEntryType
                                    _mapEntryMonitoringType.value = report.monitoringType ?: ""
                                    _mapEntryMaterial.value = report.material ?: ""
                                    _mapEntryCreated.value = report.createdTimestamp
                                    _mapEntryControlDate.value =
                                        report.nextControlDate?.toLocalDate()
                                    _mapEntryUpdated.value = report.lastUpdatedTimestamp
                                    _mapEntryDescription.value = report.description ?: ""
                                    _mapEntryResult.value = report.result ?: ""
                                    _mapEntryLatitude.value = report.latitude
                                    _mapEntryLongitude.value = report.longitude
                                    _mapEntryImageUris.value = report.imageUris.toSet()
                                    _oldMapEntry.value = report.copy()
                                }

                            }

                            MapEntry.MapEntryType.BIOTOP -> {
                                biotopUseCases.getBiotop(mapEntryId)?.also { report ->
                                    currentMapEntryId = mapEntryId
                                    _mapEntryType.value = mapEntryType
                                    _mapEntryBiotopCategory.value = report.category.value
                                    _mapEntryCreated.value = report.createdTimestamp
                                    _mapEntryUpdated.value = report.lastUpdatedTimestamp
                                    _mapEntryDescription.value = report.description ?: ""
                                    _mapEntryBiotopType.value = report.type
                                    _mapEntryLatitude.value = report.latitude
                                    _mapEntryLongitude.value = report.longitude
                                    _mapEntryImageUris.value = report.imageUris.toSet()
                                    _oldMapEntry.value = report.copy()
                                }
                            }
                        }
                    }
                }
            }
        }

        savedStateHandle.get<Float>("latitude")?.let { latitude ->
            _mapEntryLatitude.value = latitude.toDouble()

        }

        savedStateHandle.get<Float>("longitude")?.let { longitude ->
            _mapEntryLongitude.value = longitude.toDouble()

        }
    }

    private suspend fun deleteOldMapEntry() {
        if (oldMapEntry.value != null) {
            when (oldMapEntry.value!!::class) {
                SpeciesReport::class -> {
                    speciesReportUseCases.deleteSpeciesReport(listOf(oldMapEntry.value as SpeciesReport))
                }

                OtherObservation::class -> {
                    otherObservationUseCases.deleteOtherObservations(listOf(oldMapEntry.value as OtherObservation))
                }

                VisitorContact::class -> {
                    visitorContactUseCases.deleteVisitorContact(listOf(oldMapEntry.value as VisitorContact))
                }

                PlantControl::class -> {
                    plantControlUseCases.deletePlantControl(listOf(oldMapEntry.value as PlantControl))
                }

                Monitoring::class -> {
                    monitoringUseCases.deleteMonitoring(listOf(oldMapEntry.value as Monitoring))
                }

                Biotop::class -> {
                    biotopUseCases.deleteBiotops(listOf(oldMapEntry.value as Biotop))
                }

            }
        }

    }


    fun onEvent(event: AddEditMapEntryEvent) {
        when (event) {

            is AddEditMapEntryEvent.ChangePosition -> {
                _textFiledSize.value = event.value.size.toSize()
            }

            is AddEditMapEntryEvent.EnteredOrigin -> {
                _mapEntryVisitorOrigin.value = event.value
            }

            is AddEditMapEntryEvent.EnteredQuantity -> {
                _mapEntryQuantity.value = event.value
            }

            is AddEditMapEntryEvent.EnteredSpecies -> {
                _mapEntrySpeciesContent.value = event.value
            }

            is AddEditMapEntryEvent.EnteredSpeciesCategory -> {
                _mapEntrySpeciesCategory.value = event.value
                _isMapEntryCategoryExpanded.value = false
            }

            is AddEditMapEntryEvent.ToggleSpeciesCategoryTab -> {
                _isMapEntryCategoryExpanded.value = !_isMapEntryCategoryExpanded.value
            }

            is AddEditMapEntryEvent.EnteredViolation -> {
                _mapEntryVisitorViolations.value = event.value
            }

            is AddEditMapEntryEvent.AddImageUris -> {
                val currentSet = _mapEntryImageUris.value
                _mapEntryImageUris.value = currentSet + event.uris
            }


            is AddEditMapEntryEvent.DeleteImageUri -> {
                val currentSet = _mapEntryImageUris.value
                _mapEntryImageUris.value = currentSet - event.uri
            }

            is AddEditMapEntryEvent.EnteredDescription -> {
                _mapEntryDescription.value = event.value
            }


            is AddEditMapEntryEvent.ChangeMapEntryType -> {
                _mapEntryType.value = event.value
            }

            is AddEditMapEntryEvent.DeleteMapEntry -> {
                viewModelScope.launch {
                    when (mapEntryType.value) {
                        MapEntry.MapEntryType.SPECIES_REPORT -> {
                            _oldMapEntry.value?.let { mapEntry ->
                                val oldSpeciesReport = mapEntry as SpeciesReport
                                speciesReportUseCases.deleteSpeciesReport(listOf(oldSpeciesReport))
                            }
                        }

                        MapEntry.MapEntryType.PLANT_CONTROL -> {
                            _oldMapEntry.value?.let { mapEntry ->
                                val oldPlantControl = mapEntry as PlantControl
                                plantControlUseCases.deletePlantControl(listOf(oldPlantControl))
                            }
                        }

                        MapEntry.MapEntryType.OTHER_OBSERVATION -> {
                            _oldMapEntry.value?.let { mapEntry ->
                                val oldOtherObservation = mapEntry as OtherObservation
                                otherObservationUseCases.deleteOtherObservations(
                                    listOf(
                                        oldOtherObservation
                                    )
                                )
                            }

                        }

                        MapEntry.MapEntryType.VISITOR_CONTACT -> {
                            _oldMapEntry.value?.let { mapEntry ->
                                val oldVisitorContact = mapEntry as VisitorContact
                                visitorContactUseCases.deleteVisitorContact(listOf(oldVisitorContact))
                            }

                        }

                        MapEntry.MapEntryType.MONITORING -> {
                            _oldMapEntry.value?.let { mapEntry ->
                                val oldMonitoring = mapEntry as Monitoring
                                monitoringUseCases.deleteMonitoring(listOf(oldMonitoring))
                            }

                        }

                        MapEntry.MapEntryType.BIOTOP -> {
                            _oldMapEntry.value?.let { mapEntry ->
                                val oldBiotop = mapEntry as Biotop
                                biotopUseCases.deleteBiotops(listOf(oldBiotop))
                            }

                        }
                    }
                    _mapEntryImageUris.value = emptySet()
                    _eventFlow.emit(UiEvent.DeleteMapEntry)
                }
                _isDeleteDialogOpen.value = false


            }

            is AddEditMapEntryEvent.SaveMapEntry -> {
                viewModelScope.launch {
                    try {
                        val updatedTimestamp = LocalDateTime.now(ZoneOffset.UTC)
                        when (mapEntryType.value) {
                            MapEntry.MapEntryType.SPECIES_REPORT -> {
                                speciesReportUseCases.addSpeciesReport(
                                    SpeciesReport(
                                        id = currentMapEntryId,
                                        description = mapEntryDescription.value,
                                        createdTimestamp = if (mapEntryCreated.value.toEpochSecond(
                                                ZoneOffset.UTC
                                            ) == 0L
                                        ) {
                                            updatedTimestamp
                                        } else {
                                            mapEntryCreated.value
                                        },
                                        lastUpdatedTimestamp = updatedTimestamp,
                                        longitude = mapEntryLongitude.value,
                                        latitude = mapEntryLatitude.value,
                                        category = getSpeciesCategoryByName(mapEntrySpeciesCategory.value)!!,
                                        quantity = mapEntryQuantity.value.toInt(),
                                        species = mapEntrySpeciesContent.value,
                                        imageUris = mapEntryImageUris.value.toList()
                                    )
                                )
                                SpeciesReport::class

                            }

                            MapEntry.MapEntryType.PLANT_CONTROL -> {
                                plantControlUseCases.addPlantControl(
                                    PlantControl(
                                        id = currentMapEntryId,
                                        description = mapEntryDescription.value,
                                        createdTimestamp = if (mapEntryCreated.value.toEpochSecond(
                                                ZoneOffset.UTC
                                            ) == 0L
                                        ) {
                                            updatedTimestamp
                                        } else {
                                            mapEntryCreated.value
                                        },
                                        lastUpdatedTimestamp = updatedTimestamp,
                                        longitude = mapEntryLongitude.value,
                                        latitude = mapEntryLatitude.value,
                                        imageUris = mapEntryImageUris.value.toList()
                                    )
                                )
                                PlantControl::class


                            }

                            MapEntry.MapEntryType.OTHER_OBSERVATION -> {
                                otherObservationUseCases.addOtherObservation(
                                    OtherObservation(
                                        id = currentMapEntryId,
                                        description = mapEntryDescription.value,
                                        createdTimestamp = if (mapEntryCreated.value.toEpochSecond(
                                                ZoneOffset.UTC
                                            ) == 0L
                                        ) {
                                            updatedTimestamp
                                        } else {
                                            mapEntryCreated.value
                                        },
                                        lastUpdatedTimestamp = updatedTimestamp,
                                        longitude = mapEntryLongitude.value,
                                        latitude = mapEntryLatitude.value,
                                        imageUris = mapEntryImageUris.value.toList()
                                    )
                                )
                                OtherObservation::class

                            }

                            MapEntry.MapEntryType.VISITOR_CONTACT -> {
                                visitorContactUseCases.addVisitorContact(
                                    VisitorContact(
                                        id = currentMapEntryId,
                                        description = mapEntryDescription.value,
                                        createdTimestamp = if (mapEntryCreated.value.toEpochSecond(
                                                ZoneOffset.UTC
                                            ) == 0L
                                        ) {
                                            updatedTimestamp
                                        } else {
                                            mapEntryCreated.value
                                        },
                                        lastUpdatedTimestamp = updatedTimestamp,
                                        longitude = mapEntryLongitude.value,
                                        latitude = mapEntryLatitude.value,
                                        origin = mapEntryVisitorOrigin.value,
                                        violations = mapEntryVisitorViolations.value,
                                        quantity = mapEntryQuantity.value.toInt(),
                                    )
                                )
                                VisitorContact::class
                            }

                            MapEntry.MapEntryType.MONITORING -> {
                                monitoringUseCases.addMonitoring(
                                    Monitoring(
                                        id = currentMapEntryId,
                                        description = mapEntryDescription.value,
                                        createdTimestamp = if (mapEntryCreated.value.toEpochSecond(
                                                ZoneOffset.UTC
                                            ) == 0L
                                        ) {
                                            updatedTimestamp
                                        } else {
                                            mapEntryCreated.value
                                        },
                                        lastUpdatedTimestamp = updatedTimestamp,
                                        longitude = mapEntryLongitude.value,
                                        latitude = mapEntryLatitude.value,
                                        monitoringType = mapEntryMonitoringType.value,
                                        result = mapEntryResult.value,
                                        material = mapEntryMaterial.value,
                                        nextControlDate = mapEntryControlDate.value?.atStartOfDay(),
                                        imageUris = mapEntryImageUris.value.toList()
                                    )
                                )
                                Monitoring::class
                            }

                            MapEntry.MapEntryType.BIOTOP -> {
                                biotopUseCases.addBiotop(
                                    Biotop(
                                        id = currentMapEntryId,
                                        description = mapEntryDescription.value,
                                        createdTimestamp = if (mapEntryCreated.value.toEpochSecond(
                                                ZoneOffset.UTC
                                            ) == 0L
                                        ) {
                                            updatedTimestamp
                                        } else {
                                            mapEntryCreated.value
                                        },
                                        lastUpdatedTimestamp = updatedTimestamp,
                                        longitude = mapEntryLongitude.value,
                                        latitude = mapEntryLatitude.value,
                                        category = getBiotopCategoryByName(mapEntryBiotopCategory.value)!!,
                                        type = mapEntryBiotopType.value,
                                        imageUris = mapEntryImageUris.value.toList()
                                    )
                                )
                                Biotop::class
                            }
                        }.let { newMapEntryClass ->
                            oldMapEntry.value?.takeIf { newMapEntryClass != oldMapEntry.value!!::class }
                                ?.let {
                                    deleteOldMapEntry()
                                }
                        }
                        _eventFlow.emit(UiEvent.SaveMapEntry)

                    } catch (e: Exception) {
                        when (e) {
                            is InvalidOtherObservationException ->
                                _eventFlow.emit(
                                    UiEvent.ShowSnackbar(
                                        message = e.message
                                            ?: "\"Andere Beobachtung Eintrag\" konnte nicht gespeichert werden."
                                    )
                                )

                            is InvalidSpeciesReportException -> _eventFlow.emit(
                                UiEvent.ShowSnackbar(
                                    message = e.message
                                        ?: "\"Artmeldung Eintrag\" konnte nicht gespeichert werden."
                                )
                            )

                            is InvalidVisitorContactException -> _eventFlow.emit(
                                UiEvent.ShowSnackbar(
                                    message = e.message
                                        ?: "\"Besucherkontakt Eintrag\" konnte nicht gespeichert werden."
                                )
                            )

                            is InvalidPlantControlException -> _eventFlow.emit(
                                UiEvent.ShowSnackbar(
                                    message = e.message
                                        ?: "\"Anlagenkontrolle Eintrag\" konnte nicht gespeichert werden."
                                )
                            )

                            is InvalidMonitoringException -> _eventFlow.emit(
                                UiEvent.ShowSnackbar(
                                    message = e.message
                                        ?: "\"Monitoring Eintrag\" konnte nicht gespeichert werden."
                                )
                            )

                            is InvalidBiotopException -> _eventFlow.emit(
                                UiEvent.ShowSnackbar(
                                    message = e.message
                                        ?: "\"Biotop Eintrag\" konnte nicht gespeichert werden."
                                )
                            )

                            is NumberFormatException -> _eventFlow.emit(
                                UiEvent.ShowSnackbar(
                                    message = "\"Eintrag\" konnte nicht gespeichert werden. " +
                                            "Bitte geben Sie in die numerischen Textfelder nur ganze Zahlen ein. "
                                )
                            )

                            else -> {
                            }
                        }

                    }
                }
            }

            is AddEditMapEntryEvent.SavePictureFromCamera -> {
                viewModelScope.launch {

                    val uri = fileStorageUseCases.saveTempUri(event.value)
                    uri?.let {
                        val currentSet = _mapEntryImageUris.value
                        _mapEntryImageUris.value = currentSet + uri
                    }
                }
            }

            is AddEditMapEntryEvent.ShareMapEntry -> {
                viewModelScope.launch {
                    _oldMapEntry.value?.let { it ->
                        fileStorageUseCases.exportJson(listOf(it), true)?.let { uri ->
                            _eventFlow.emit(UiEvent.ShareJson(uri))
                        }
                    }
                }

            }

            AddEditMapEntryEvent.CloseDeleteDialog -> _isDeleteDialogOpen.value = false
            AddEditMapEntryEvent.OpenDeleteDialog -> _isDeleteDialogOpen.value = true
            is AddEditMapEntryEvent.TriggerError -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ShowSnackbar(event.message))
                }

            }

            is AddEditMapEntryEvent.EnteredMaterial -> _mapEntryMaterial.value = event.value
            is AddEditMapEntryEvent.EnteredMonitoringType -> _mapEntryMonitoringType.value =
                event.value

            is AddEditMapEntryEvent.EnteredResult -> _mapEntryResult.value = event.value
            AddEditMapEntryEvent.ToggleBiotopCategoryTab -> _isBiotopCategoryExpanded.value =
                !_isBiotopCategoryExpanded.value

            is AddEditMapEntryEvent.EnteredBiotopType -> _mapEntryBiotopType.value = event.value
            is AddEditMapEntryEvent.EnteredBiotopCategory -> {
                _mapEntryBiotopCategory.value = event.value
                _isBiotopCategoryExpanded.value = false
            }

            is AddEditMapEntryEvent.EnteredControlDate -> {
                _mapEntryControlDate.value = event.value
            }
        }
    }

    sealed class UiEvent {
        data class ShareJson(val uri: Uri) : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
        object SaveMapEntry : UiEvent()
        object DeleteMapEntry : UiEvent()
        data class RequestPermissionForDeletion(val intentSender: IntentSender) : UiEvent()
    }
}