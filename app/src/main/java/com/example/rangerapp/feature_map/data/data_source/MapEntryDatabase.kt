package com.example.rangerapp.feature_map.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.rangerapp.feature_map.domain.model.*
import com.example.rangerapp.feature_map.domain.util.Converters

@Database(
    entities = [PlantControl::class, OtherObservation::class,
        SpeciesReport::class, VisitorContact::class, Biotop::class, Monitoring::class],
    version = 15
)
@TypeConverters(Converters::class)
abstract class MapEntryDatabase : RoomDatabase() {
    abstract val plantControlDao: PlantControlDao
    abstract val visitorContactDao: VisitorContactDao
    abstract val otherObservationDao: OtherObservationDao
    abstract val speciesReportDao: SpeciesReportDao
    abstract val mapEntryMarkerDao: MapEntryMarkerDao
    abstract val monitoringDao: MonitoringDao
    abstract val biotopDao: BiotopDao


    companion object {
        const val DATABASE_NAME = "map_entries_db"
    }
}

