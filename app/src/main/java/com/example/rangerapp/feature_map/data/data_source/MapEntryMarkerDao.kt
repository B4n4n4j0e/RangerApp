package com.example.rangerapp.feature_map.data.data_source

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.rangerapp.feature_map.domain.model.*
import kotlinx.coroutines.flow.Flow


@Dao
interface MapEntryMarkerDao {
    @RawQuery(observedEntities = [OtherObservation::class, PlantControl::class, SpeciesReport::class, VisitorContact::class, Biotop::class, Monitoring::class])
    fun getAll(query: SupportSQLiteQuery): Flow<List<MapEntryMarker>>


}