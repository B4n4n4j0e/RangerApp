package com.example.rangerapp.feature_map.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.rangerapp.feature_map.data.data_source.VisitorContactDao
import com.example.rangerapp.feature_map.domain.model.MapEntry
import com.example.rangerapp.feature_map.domain.model.UpdateMapEntry
import com.example.rangerapp.feature_map.domain.model.VisitorContact
import com.example.rangerapp.feature_map.domain.repository.VisitorContactRepository
import java.time.LocalDate
import java.time.LocalDateTime

class VisitorContactRepositoryImpl(private val dao: VisitorContactDao) : VisitorContactRepository {

    override suspend fun getAll(
        createdStartDate: LocalDate?,
        createdEndDate: LocalDate?,
        updatedStartDate: LocalDate?,
        updatedEndDate: LocalDate?,
        searchTerm: String
    ): List<VisitorContact> {
        val queryString = StringBuilder()
        val queryParams = mutableListOf<String>()
        val searchAttributes = MapEntry.MapEntryType.VISITOR_CONTACT.search_attributes

        queryString.append("SELECT * from visitor_contact")
        queryString.append(
            QueryHelper.createQueryString(
                createdStartDate,
                createdEndDate,
                updatedStartDate,
                updatedEndDate,
                searchTerm,
                searchAttributes
            )
        )
        queryParams.addAll(
            QueryHelper.createParamList(
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

    override suspend fun getById(id: Int): VisitorContact? {
        return dao.getById(id)

    }

    override suspend fun getByIndex(
        createdTimestamp: LocalDateTime,
        latitude: Double,
        longitude: Double
    ): VisitorContact? {
        return dao.getByIndex(createdTimestamp, latitude, longitude)
    }

    override suspend fun insert(visitorContact: VisitorContact) {
        dao.insert(visitorContact)
    }

    override suspend fun delete(visitorContacts: List<VisitorContact>) {
        dao.delete(visitorContacts)
    }

    override suspend fun update(updateMapEntry: UpdateMapEntry) {
        dao.updateLocation(updateMapEntry)
    }

    override suspend fun insertIgnore(visitorContact: VisitorContact): Long {
        return dao.insertIgnore(visitorContact)
    }


}