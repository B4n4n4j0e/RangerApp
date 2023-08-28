package com.example.rangerapp.feature_map.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.rangerapp.feature_map.data.data_source.BiotopDao
import com.example.rangerapp.feature_map.data.repository.QueryHelper.Companion.createParamList
import com.example.rangerapp.feature_map.data.repository.QueryHelper.Companion.createQueryString
import com.example.rangerapp.feature_map.domain.model.Biotop
import com.example.rangerapp.feature_map.domain.model.MapEntry
import com.example.rangerapp.feature_map.domain.model.UpdateMapEntry
import com.example.rangerapp.feature_map.domain.repository.BiotopRepository
import java.time.LocalDate
import java.time.LocalDateTime

class BiotopRepositoryImpl(private val dao: BiotopDao) : BiotopRepository {

    override suspend fun getAll(
        createdStartDate: LocalDate?,
        createdEndDate: LocalDate?,
        updatedStartDate: LocalDate?,
        updatedEndDate: LocalDate?,
        searchTerm: String
    ): List<Biotop> {
        val queryString = StringBuilder()
        val queryParams = mutableListOf<String>()
        val searchAttributes = MapEntry.MapEntryType.BIOTOP.search_attributes

        queryString.append("SELECT * from biotop")
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
    ): Biotop? {
        return dao.getByIndex(createdTimestamp, latitude, longitude)
    }

    override suspend fun getById(id: Int): Biotop? {
        return dao.getById(id)

    }

    override suspend fun insert(biotop: Biotop) {
        dao.insert(biotop)
    }

    override suspend fun delete(biotopList: List<Biotop>) {
        dao.delete(biotopList)
    }

    override suspend fun update(updateMapEntry: UpdateMapEntry) {
        dao.updateLocation(updateMapEntry)
    }

    override suspend fun insertIgnore(biotop: Biotop): Long {
        return dao.insertIgnore(biotop)
    }


}