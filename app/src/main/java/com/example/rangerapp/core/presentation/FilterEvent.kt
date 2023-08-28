package com.example.rangerapp.core.presentation

import com.example.rangerapp.feature_map.domain.model.MapEntry
import java.time.LocalDate

sealed class FilterEvent {

    data class ChangeCreatedStartDate(val date: LocalDate?) : FilterEvent()
    data class ChangeCreatedEndDate(val date: LocalDate?) : FilterEvent()
    data class ChangeUpdatedStartDate(val date: LocalDate?) : FilterEvent()
    data class ChangeUpdatedEndDateDate(val date: LocalDate?) : FilterEvent()

    data class ChangeControlStartDate(val date: LocalDate?) : FilterEvent()
    data class ChangeControlEndDateDate(val date: LocalDate?) : FilterEvent()

    data class ChangeSearchBar(val value: String) : FilterEvent()
    object ClearFilter : FilterEvent()
    object ToggleFilterView : FilterEvent()
    data class UpdateMapEntryTypeFilter(val value: MapEntry.MapEntryType) : FilterEvent()


}