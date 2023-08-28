package com.example.rangerapp.feature_map.data.data_source

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.rangerapp.feature_map.domain.model.Monitoring
import com.example.rangerapp.feature_map.domain.model.UpdateMapEntry
import java.time.LocalDateTime


@Dao
interface MonitoringDao : BaseDao<Monitoring> {
    @RawQuery
    suspend fun getAll(query: SupportSQLiteQuery): List<Monitoring>

    @Query("SELECT * FROM monitoring WHERE id = :id")
    suspend fun getById(id: Int): Monitoring?

    @Update(entity = Monitoring::class)
    suspend fun updateLocation(updateMapEntry: UpdateMapEntry)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(monitoring: Monitoring): Long

    @Query("SELECT * FROM monitoring WHERE created_timestamp = :createdTimestamp AND longitude = :longitude AND latitude = :latitude")
    suspend fun getByIndex(
        createdTimestamp: LocalDateTime,
        latitude: Double,
        longitude: Double
    ): Monitoring?
}