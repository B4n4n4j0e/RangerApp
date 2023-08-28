package com.example.rangerapp.feature_map.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.rangerapp.feature_map.data.data_source.PlantControlDao
import com.example.rangerapp.feature_map.domain.model.MapEntry
import com.example.rangerapp.feature_map.domain.model.PlantControl
import com.example.rangerapp.feature_map.domain.model.UpdateMapEntry
import com.example.rangerapp.feature_map.domain.repository.PlantControlRepository
import java.time.LocalDate
import java.time.LocalDateTime

class PlantControlRepositoryImpl(private val dao: PlantControlDao) : PlantControlRepository {

    override suspend fun getAll(
        createdStartDate: LocalDate?,
        createdEndDate: LocalDate?,
        updatedStartDate: LocalDate?,
        updatedEndDate: LocalDate?,
        searchTerm: String
    ): List<PlantControl> {
        val queryString = StringBuilder()
        val queryParams = mutableListOf<String>()
        val searchAttributes = MapEntry.MapEntryType.PLANT_CONTROL.search_attributes

        queryString.append("SELECT * from plant_control")
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

    override suspend fun getByIndex(
        createdTimestamp: LocalDateTime,
        latitude: Double,
        longitude: Double
    ): PlantControl? {
        return dao.getByIndex(createdTimestamp, latitude, longitude)
    }


    override suspend fun getById(id: Int): PlantControl? {
        return dao.getById(id)

    }

    override suspend fun insert(plantControl: PlantControl) {
        dao.insert(plantControl)
    }

    override suspend fun delete(plantControls: List<PlantControl>) {
        dao.delete(plantControls)
    }

    override suspend fun update(updateMapEntry: UpdateMapEntry) {
        dao.updateLocation(updateMapEntry)
    }

    override suspend fun insertIgnore(plantControl: PlantControl): Long {
        return dao.insertIgnore(plantControl)
    }


}