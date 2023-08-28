package com.example.rangerapp.feature_map.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.rangerapp.feature_map.data.data_source.OtherObservationDao
import com.example.rangerapp.feature_map.domain.model.MapEntry
import com.example.rangerapp.feature_map.domain.model.OtherObservation
import com.example.rangerapp.feature_map.domain.model.UpdateMapEntry
import com.example.rangerapp.feature_map.domain.repository.OtherObservationRepository
import java.time.LocalDate
import java.time.LocalDateTime

class OtherObservationRepositoryImpl(private val dao: OtherObservationDao) :
    OtherObservationRepository {

    override suspend fun getAll(
        createdStartDate: LocalDate?,
        createdEndDate: LocalDate?,
        updatedStartDate: LocalDate?,
        updatedEndDate: LocalDate?,
        searchTerm: String
    ): List<OtherObservation> {
        val queryString = StringBuilder()
        val queryParams = mutableListOf<String>()
        val searchAttributes = MapEntry.MapEntryType.OTHER_OBSERVATION.search_attributes

        queryString.append("SELECT * from other_observation")
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


    override suspend fun getById(id: Int): OtherObservation? {
        return dao.getById(id)

    }

    override suspend fun getByIndex(
        createdTimestamp: LocalDateTime,
        latitude: Double,
        longitude: Double
    ): OtherObservation? {
        return dao.getByIndex(createdTimestamp, latitude, longitude)
    }

    override suspend fun insert(otherObservation: OtherObservation) {
        dao.insert(otherObservation)
    }

    override suspend fun delete(otherObservations: List<OtherObservation>) {
        dao.delete(otherObservations)
    }

    override suspend fun update(updateMapEntry: UpdateMapEntry) {
        dao.updateLocation(updateMapEntry)
    }

    override suspend fun insertIgnore(otherObservation: OtherObservation): Long {
        return dao.insertIgnore(otherObservation)
    }


}