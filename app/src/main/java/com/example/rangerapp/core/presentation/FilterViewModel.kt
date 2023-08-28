package com.example.rangerapp.feature_map.presentation.map_entries


import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.rangerapp.core.presentation.FilterEvent
import com.example.rangerapp.feature_map.domain.model.MapEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
) : ViewModel() {


    private val _mapFilter =
        mutableStateOf(enumValues<MapEntry.MapEntryType>().toSet())
    val mapFilter: State<Set<MapEntry.MapEntryType>> = _mapFilter

    private val _searchBar = mutableStateOf("")
    val searchBar: State<String> = _searchBar


    private val _isFilterSectionVisible = mutableStateOf<Boolean>(false)
    val isFilterSectionVisible: State<Boolean> = _isFilterSectionVisible
    private val _startCreatedDate = mutableStateOf<LocalDate?>(null)
    val startCreatedDate: State<LocalDate?> = _startCreatedDate
    private val _endCreatedDate = mutableStateOf<LocalDate?>(null)
    val endCreatedDate: State<LocalDate?> = _endCreatedDate
    private val _startUpdatedDate = mutableStateOf<LocalDate?>(null)
    val startUpdatedDate: State<LocalDate?> = _startUpdatedDate
    private val _endUpdatedDate = mutableStateOf<LocalDate?>(null)
    val endUpdatedDate: State<LocalDate?> = _endUpdatedDate

    private val _startControlDate = mutableStateOf<LocalDate?>(null)
    val startControlDate: State<LocalDate?> = _startControlDate
    private val _endControlDate = mutableStateOf<LocalDate?>(null)
    val endControlDate: State<LocalDate?> = _endControlDate

    fun onEvent(event: FilterEvent) {

        when (event) {
            is FilterEvent.ChangeCreatedEndDate ->
                _endCreatedDate.value = event.date

            is FilterEvent.ChangeCreatedStartDate ->
                _startCreatedDate.value = event.date

            is FilterEvent.ChangeSearchBar ->
                _searchBar.value = event.value

            is FilterEvent.ChangeUpdatedEndDateDate ->
                _endUpdatedDate.value = event.date

            is FilterEvent.ChangeUpdatedStartDate ->
                _startUpdatedDate.value = event.date

            FilterEvent.ClearFilter -> {
                _startUpdatedDate.value = null
                _endUpdatedDate.value = null
                _searchBar.value = ""
                _startCreatedDate.value = null
                _endCreatedDate.value = null
                _startControlDate.value = null
                _endControlDate.value = null
                _mapFilter.value = enumValues<MapEntry.MapEntryType>().toSet()
                _isFilterSectionVisible.value = !_isFilterSectionVisible.value

            }

            FilterEvent.ToggleFilterView -> {
                _isFilterSectionVisible.value = !_isFilterSectionVisible.value
            }

            is FilterEvent.UpdateMapEntryTypeFilter -> {
                if (_mapFilter.value.contains(event.value)) {
                    _mapFilter.value = _mapFilter.value - event.value
                } else {
                    _mapFilter.value = _mapFilter.value + event.value
                }
            }

            is FilterEvent.ChangeControlEndDateDate -> {
                _endControlDate.value = event.date
                if (event.date == null) {
                    return
                }
                _mapFilter.value = setOf(MapEntry.MapEntryType.MONITORING)
            }

            is FilterEvent.ChangeControlStartDate -> {
                _startControlDate.value = event.date
                if (event.date == null) {
                    return
                }
                _mapFilter.value = setOf(MapEntry.MapEntryType.MONITORING)

            }
        }


    }
}