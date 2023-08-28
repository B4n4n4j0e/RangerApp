package com.example.rangerapp.feature_map.data.data_source

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.rangerapp.feature_map.domain.model.OtherObservation
import com.example.rangerapp.feature_map.domain.model.UpdateMapEntry
import java.time.LocalDateTime


@Dao
interface OtherObservationDao : BaseDao<OtherObservation> {
    @RawQuery
    suspend fun getAll(query: SupportSQLiteQuery): List<OtherObservation>

    @Query("SELECT * FROM other_observation WHERE id = :id")
    suspend fun getById(id: Int): OtherObservation?

    @Update(entity = OtherObservation::class)
    suspend fun updateLocation(updateMapEntry: UpdateMapEntry)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(otherObservation: OtherObservation): Long

    @Query("SELECT * FROM other_observation WHERE created_timestamp = :createdTimestamp AND longitude = :longitude AND latitude = :latitude")
    suspend fun getByIndex(
        createdTimestamp: LocalDateTime,
        latitude: Double,
        longitude: Double
    ): OtherObservation?
}