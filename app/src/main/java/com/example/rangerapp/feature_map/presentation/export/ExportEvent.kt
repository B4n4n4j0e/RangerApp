package com.example.rangerapp.feature_map.presentation.export

import android.net.Uri
import com.example.rangerapp.feature_map.domain.model.MapEntry
import java.time.LocalDate

sealed class ExportEvent {


    data class ImportJson(val uri: Uri) : ExportEvent()
    data class ExportJson(
        val mapEntryTypes: Set<MapEntry.MapEntryType>,
        val startCreatedDate: LocalDate?,
        val endCreatedDate: LocalDate?,
        val startUpdatedDate: LocalDate?,
        val endUpdatedDate: LocalDate?,
        val startControlDate: LocalDate?,
        val endControlDate: LocalDate?,
        val searchTerm: String,
        val withPictures: Boolean
    ) : ExportEvent()

    data class RequestDeletion(
        val mapEntryTypes: Set<MapEntry.MapEntryType>,
        val startCreatedDate: LocalDate?,
        val endCreatedDate: LocalDate?,
        val startUpdatedDate: LocalDate?,
        val endUpdatedDate: LocalDate?,
        val startControlDate: LocalDate?,
        val endControlDate: LocalDate?,
        val searchTerm: String
    ) : ExportEvent()

    object ConfirmDeletion : ExportEvent()
    object DismissDeletion : ExportEvent()

    data class ExportCsv(
        val mapEntryTypes: Set<MapEntry.MapEntryType>,
        val startCreatedDate: LocalDate?,
        val endCreatedDate: LocalDate?,
        val startUpdatedDate: LocalDate?,
        val endUpdatedDate: LocalDate?,
        val startControlDate: LocalDate?,
        val endControlDate: LocalDate?,
        val searchTerm: String
    ) : ExportEvent()


    data class SelectImportedConflictEntry(val mapEntryPair: Pair<MapEntry, MapEntry>) :
        ExportEvent()

    data class SelectCurrentConflictEntry(val mapEntryPair: Pair<MapEntry, MapEntry>) :
        ExportEvent()

}