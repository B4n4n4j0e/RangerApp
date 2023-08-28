package com.example.rangerapp.feature_map.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.rangerapp.feature_map.data.data_source.MonitoringDao
import com.example.rangerapp.feature_map.data.repository.QueryHelper.Companion.createParamList
import com.example.rangerapp.feature_map.data.repository.QueryHelper.Companion.createQueryString
import com.example.rangerapp.feature_map.domain.model.MapEntry
import com.example.rangerapp.feature_map.domain.model.Monitoring
import com.example.rangerapp.feature_map.domain.model.UpdateMapEntry
import com.example.rangerapp.feature_map.domain.repository.MonitoringRepository
import java.time.LocalDate
import java.time.LocalDateTime

class MonitoringRepositoryImpl(private val dao: MonitoringDao) : MonitoringRepository {

    override suspend fun getAll(
        createdStartDate: LocalDate?,
        createdEndDate: LocalDate?,
        updatedStartDate: LocalDate?,
        updatedEndDate: LocalDate?,
        startControlDate: LocalDate?,
        endControlDate: LocalDate?,
        searchTerm: String
    ): List<Monitoring> {
        val queryString = StringBuilder()
        val queryParams = mutableListOf<String>()
        val searchAttributes = MapEntry.MapEntryType.MONITORING.search_attributes

        queryString.append("SELECT * from monitoring")
        queryString.append(
            createQueryString(
                createdStartDate,
                createdEndDate,
                updatedStartDate,
                updatedEndDate,
                searchTerm,
                searchAttributes,
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
                searchAttributes,
                startControlDate,
                endControlDate
            )
        )

        return dao.getAll(
            SimpleSQLiteQuery(queryString.toString(), queryParams.toTypedArray())
        )
    }

    override suspend fun getByIndex(
        createdTimestamp: LocalDateTime,
        latitude: Double,
        longitude: Double
    ): Monitoring? {
        return dao.getByIndex(createdTimestamp, latitude, longitude)
    }

    override suspend fun getById(id: Int): Monitoring? {
        return dao.getById(id)

    }

    override suspend fun insert(monitoring: Monitoring) {
        dao.insert(monitoring)
    }

    override suspend fun delete(monitoringList: List<Monitoring>) {
        dao.delete(monitoringList)
    }

    override suspend fun update(updateMapEntry: UpdateMapEntry) {
        dao.updateLocation(updateMapEntry)
    }

    override suspend fun insertIgnore(monitoring: Monitoring): Long {
        return dao.insertIgnore(monitoring)
    }

}