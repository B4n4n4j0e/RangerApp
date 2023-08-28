package com.example.rangerapp.feature_map.data.data_source

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.rangerapp.feature_map.domain.model.PlantControl
import com.example.rangerapp.feature_map.domain.model.UpdateMapEntry
import java.time.LocalDateTime


@Dao
interface PlantControlDao : BaseDao<PlantControl> {
    @RawQuery
    suspend fun getAll(query: SupportSQLiteQuery): List<PlantControl>

    @Query("SELECT * FROM plant_control WHERE id = :id")
    suspend fun getById(id: Int): PlantControl?

    @Update(entity = PlantControl::class)
    suspend fun updateLocation(updateMapEntry: UpdateMapEntry)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(plantControl: PlantControl): Long

    @Query("SELECT * FROM plant_control WHERE created_timestamp = :createdTimestamp AND longitude = :longitude AND latitude = :latitude")
    suspend fun getByIndex(
        createdTimestamp: LocalDateTime,
        latitude: Double,
        longitude: Double
    ): PlantControl?
}