package com.example.rangerapp.feature_map.presentation.add_edit_entity.mapEntry

import android.net.Uri
import androidx.compose.ui.layout.LayoutCoordinates
import com.example.rangerapp.feature_map.domain.model.MapEntry
import java.time.LocalDate

sealed class AddEditMapEntryEvent {


    data class EnteredDescription(val value: String) : AddEditMapEntryEvent()
    data class EnteredQuantity(val value: String) : AddEditMapEntryEvent()
    data class EnteredSpecies(val value: String) : AddEditMapEntryEvent()
    data class EnteredSpeciesCategory(val value: String) : AddEditMapEntryEvent()
    data class EnteredOrigin(val value: String) : AddEditMapEntryEvent()
    data class EnteredViolation(val value: String) : AddEditMapEntryEvent()
    data class EnteredMonitoringType(val value: String) : AddEditMapEntryEvent()
    data class EnteredMaterial(val value: String) : AddEditMapEntryEvent()
    data class EnteredResult(val value: String) : AddEditMapEntryEvent()
    data class EnteredBiotopType(val value: String) : AddEditMapEntryEvent()
    data class EnteredBiotopCategory(val value: String) : AddEditMapEntryEvent()
    data class EnteredControlDate(val value: LocalDate?) : AddEditMapEntryEvent()


    data class SavePictureFromCamera(val value: Uri) : AddEditMapEntryEvent()
    object ShareMapEntry : AddEditMapEntryEvent()
    object OpenDeleteDialog : AddEditMapEntryEvent()
    object CloseDeleteDialog : AddEditMapEntryEvent()
    data class TriggerError(val message: String) : AddEditMapEntryEvent()

    object ToggleSpeciesCategoryTab : AddEditMapEntryEvent()
    object ToggleBiotopCategoryTab : AddEditMapEntryEvent()

    data class ChangePosition(val value: LayoutCoordinates) : AddEditMapEntryEvent()
    data class ChangeMapEntryType(val value: MapEntry.MapEntryType) : AddEditMapEntryEvent()

    data class AddImageUris(val uris: List<Uri>) : AddEditMapEntryEvent()
    data class DeleteImageUri(val uri: Uri) : AddEditMapEntryEvent()

    object SaveMapEntry : AddEditMapEntryEvent()
    object DeleteMapEntry : AddEditMapEntryEvent()

}