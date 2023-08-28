package com.example.rangerapp.feature_map.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.rangerapp.feature_map.data.data_source.SpeciesReportDao
import com.example.rangerapp.feature_map.data.repository.QueryHelper.Companion.createParamList
import com.example.rangerapp.feature_map.data.repository.QueryHelper.Companion.createQueryString
import com.example.rangerapp.feature_map.domain.model.MapEntry
import com.example.rangerapp.feature_map.domain.model.SpeciesReport
import com.example.rangerapp.feature_map.domain.model.UpdateMapEntry
import com.example.rangerapp.feature_map.domain.repository.SpeciesReportRepository
import java.time.LocalDate
import java.time.LocalDateTime

class SpeciesReportRepositoryImpl(private val dao: SpeciesReportDao) : SpeciesReportRepository {

    override suspend fun getAll(
        createdStartDate: LocalDate?,
        createdEndDate: LocalDate?,
        updatedStartDate: LocalDate?,
        updatedEndDate: LocalDate?,
        searchTerm: String
    ): List<SpeciesReport> {
        val queryString = StringBuilder()
        val queryParams = mutableListOf<String>()
        val searchAttributes = MapEntry.MapEntryType.SPECIES_REPORT.search_attributes

        queryString.append("SELECT * from species_report")
        queryString.append(
            createQueryString(
                createdStartDate,
                createdEndDate,
                updatedStartDate,
                updatedEndDate,
                searchTerm,
                searchAttributes
            )
        )
        queryParams.addAll(
            createParamList(
                createdStartDate,
                createdEndDate,
                updatedStartDate,
                updatedEndDate,
                searchTerm,
                searchAttributes
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
    ): SpeciesReport? {
        return dao.getByIndex(createdTimestamp, latitude, longitude)
    }

    override suspend fun getById(id: Int): SpeciesReport? {
        return dao.getById(id)

    }

    override suspend fun insert(speciesReport: SpeciesReport) {
        dao.insert(speciesReport)
    }

    override suspend fun delete(speciesReports: List<SpeciesReport>) {
        dao.delete(speciesReports)
    }

    override suspend fun update(updateMapEntry: UpdateMapEntry) {
        dao.updateLocation(updateMapEntry)
    }

    override suspend fun insertIgnore(speciesReport: SpeciesReport): Long {
        return dao.insertIgnore(speciesReport)
    }


}