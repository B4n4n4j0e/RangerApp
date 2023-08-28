package com.example.rangerapp.feature_map.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.rangerapp.feature_map.data.data_source.MapEntryMarkerDao
import com.example.rangerapp.feature_map.data.repository.QueryHelper.Companion.createParamList
import com.example.rangerapp.feature_map.data.repository.QueryHelper.Companion.createQueryString
import com.example.rangerapp.feature_map.domain.model.MapEntry
import com.example.rangerapp.feature_map.domain.model.MapEntryMarker
import com.example.rangerapp.feature_map.domain.repository.MapEntryMarkerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate


class MapEntryMarkerRepositoryImpl(private val dao: MapEntryMarkerDao) : MapEntryMarkerRepository {

    override fun getAll(
        filter: Set<MapEntry.MapEntryType>,
        createdStartDate: LocalDate?,
        createdEndDate: LocalDate?,
        updatedStartDate: LocalDate?,
        updatedEndDate: LocalDate?,
        searchTerm: String,
        startControlDate: LocalDate?,
        endControlDate: LocalDate?,
    ): Flow<List<MapEntryMarker>> {
        return if (filter.isEmpty()) {
            flowOf(emptyList())
        } else {
            var updatedFilter = filter
            if (startControlDate != null || endControlDate != null) {
                updatedFilter = setOf(MapEntry.MapEntryType.MONITORING)
            }
            val queryString = StringBuilder()
            val firstItem = updatedFilter.first()
            val queryParams = mutableListOf<String>()
            val updatedList = updatedFilter.drop(1)
            queryString.append("SELECT id, ${firstItem.title} as title, description, longitude, latitude, '${firstItem.name}' as mapEntryType, created_timestamp as createdTimestamp from ${firstItem.table_name}")
            queryString.append(
                createQueryString(
                    createdStartDate,
                    createdEndDate,
                    updatedStartDate,
                    updatedEndDate,
                    searchTerm,
                    firstItem.search_attributes,
                    startControlDate,
                    endControlDate
                )
            )
            queryParams.addAll(
                createParamList(
                    createdStartDate,
                    createdEndDate,
                    updatedStartDate,
                    updatedEndDate,
                    searchTerm,
                    firstItem.search_attributes,
                    startControlDate,
                    endControlDate
                )
            )

            for (table in updatedList) {
                queryString.append(" UNION SELECT id, ${table.title} as title, description, longitude, latitude, '${table.name}' as mapEntryType, created_timestamp as createdTimestamp FROM ${table.table_name}")
                queryString.append(
                    createQueryString(
                        createdStartDate,
                        createdEndDate,
                        updatedStartDate,
                        updatedEndDate,
                        searchTerm,
                        table.search_attributes,
                        startControlDate,
                        endControlDate
                    )
                )
                queryParams.addAll(
                    createParamList(
                        createdStartDate,
                        createdEndDate,
                        updatedStartDate,
                        updatedEndDate,
                        searchTerm,
                        table.search_attributes,
                        startControlDate,
                        endControlDate
                    )
                )

            }
            dao.getAll(
                SimpleSQLiteQuery(queryString.toString(), queryParams.toTypedArray())
            )
        }

    }

}





