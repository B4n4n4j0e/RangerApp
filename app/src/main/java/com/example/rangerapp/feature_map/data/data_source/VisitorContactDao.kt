package com.example.rangerapp.feature_map.data.data_source

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.rangerapp.feature_map.domain.model.UpdateMapEntry
import com.example.rangerapp.feature_map.domain.model.VisitorContact
import java.time.LocalDateTime


@Dao
interface VisitorContactDao : BaseDao<VisitorContact> {
    @RawQuery
    suspend fun getAll(query: SupportSQLiteQuery): List<VisitorContact>

    @Query("SELECT * FROM visitor_contact WHERE id = :id")
    suspend fun getById(id: Int): VisitorContact?

    @Update(entity = VisitorContact::class)
    suspend fun updateLocation(updateMapEntry: UpdateMapEntry)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(visitorContact: VisitorContact): Long

    @Query("SELECT * FROM visitor_contact WHERE created_timestamp = :createdTimestamp AND longitude = :longitude AND latitude = :latitude")
    suspend fun getByIndex(
        createdTimestamp: LocalDateTime,
        latitude: Double,
        longitude: Double
    ): VisitorContact?
}