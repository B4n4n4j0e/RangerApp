package com.example.rangerapp.feature_map.data.data_source

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.rangerapp.feature_map.domain.model.Biotop
import com.example.rangerapp.feature_map.domain.model.UpdateMapEntry
import java.time.LocalDateTime


@Dao
interface BiotopDao : BaseDao<Biotop> {
    @RawQuery
    suspend fun getAll(query: SupportSQLiteQuery): List<Biotop>

    @Query("SELECT * FROM biotop WHERE id = :id")
    suspend fun getById(id: Int): Biotop?

    @Update(entity = Biotop::class)
    suspend fun updateLocation(updateMapEntry: UpdateMapEntry)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(biotop: Biotop): Long

    @Query("SELECT * FROM biotop WHERE created_timestamp = :createdTimestamp AND longitude = :longitude AND latitude = :latitude")
    suspend fun getByIndex(
        createdTimestamp: LocalDateTime,
        latitude: Double,
        longitude: Double
    ): Biotop?

}