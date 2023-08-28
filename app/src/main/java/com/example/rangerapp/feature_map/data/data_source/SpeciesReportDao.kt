package com.example.rangerapp.feature_map.data.data_source

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.rangerapp.feature_map.domain.model.SpeciesReport
import com.example.rangerapp.feature_map.domain.model.UpdateMapEntry
import java.time.LocalDateTime


@Dao
interface SpeciesReportDao : BaseDao<SpeciesReport> {
    @RawQuery
    suspend fun getAll(query: SupportSQLiteQuery): List<SpeciesReport>

    @Query("SELECT * FROM species_report WHERE id = :id")
    suspend fun getById(id: Int): SpeciesReport?

    @Update(entity = SpeciesReport::class)
    suspend fun updateLocation(updateMapEntry: UpdateMapEntry)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(speciesReport: SpeciesReport): Long

    @Query("SELECT * FROM species_report WHERE created_timestamp = :createdTimestamp AND longitude = :longitude AND latitude = :latitude")
    suspend fun getByIndex(
        createdTimestamp: LocalDateTime,
        latitude: Double,
        longitude: Double
    ): SpeciesReport?

}