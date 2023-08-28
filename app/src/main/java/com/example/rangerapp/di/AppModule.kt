package com.example.rangerapp.di

import android.app.Application
import androidx.room.Room
import com.example.rangerapp.feature_map.data.data_source.MapEntryDatabase
import com.example.rangerapp.feature_map.data.repository.*
import com.example.rangerapp.feature_map.domain.repository.*
import com.example.rangerapp.feature_map.domain.use_case.*
import com.example.rangerapp.feature_map.domain.use_case.biotop.*
import com.example.rangerapp.feature_map.domain.use_case.file.*
import com.example.rangerapp.feature_map.domain.use_case.marker.GetMapEntryMarkers
import com.example.rangerapp.feature_map.domain.use_case.marker.MapEntryMarkerUseCases
import com.example.rangerapp.feature_map.domain.use_case.monitoring.*
import com.example.rangerapp.feature_map.domain.use_case.other_observation.*
import com.example.rangerapp.feature_map.domain.use_case.plant_control.*
import com.example.rangerapp.feature_map.domain.use_case.species_report.*
import com.example.rangerapp.feature_map.domain.use_case.visitor_contact.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideMapEntryDatabase(app: Application): MapEntryDatabase {
        return Room.databaseBuilder(
            app,
            MapEntryDatabase::class.java,
            MapEntryDatabase.DATABASE_NAME
            //Destroys old Database if new version of database exists
        ).fallbackToDestructiveMigration()
            .build()
    }


    @Provides
    @Singleton
    fun provideBiotopRepository(db: MapEntryDatabase): BiotopRepository {
        return BiotopRepositoryImpl(db.biotopDao)
    }

    @Provides
    @Singleton
    fun provideMonitoringRepository(db: MapEntryDatabase): MonitoringRepository {
        return MonitoringRepositoryImpl(db.monitoringDao)
    }


    @Provides
    @Singleton
    fun providePlantControlRepository(db: MapEntryDatabase): PlantControlRepository {
        return PlantControlRepositoryImpl(db.plantControlDao)
    }

    @Provides
    @Singleton
    fun provideVisitorContactRepository(db: MapEntryDatabase): VisitorContactRepository {
        return VisitorContactRepositoryImpl(db.visitorContactDao)
    }

    @Provides
    @Singleton
    fun provideSpeciesReportRepository(db: MapEntryDatabase): SpeciesReportRepository {
        return SpeciesReportRepositoryImpl(db.speciesReportDao)
    }

    @Provides
    @Singleton
    fun provideOtherObservationRepository(db: MapEntryDatabase): OtherObservationRepository {
        return OtherObservationRepositoryImpl(db.otherObservationDao)
    }

    @Provides
    @Singleton
    fun provideMapEntryMarkerRepository(db: MapEntryDatabase): MapEntryMarkerRepository {
        return MapEntryMarkerRepositoryImpl(db.mapEntryMarkerDao)
    }

    @Provides
    @Singleton
    fun provideFileRepository(app: Application): FileRepository {
        return FileRepositoryImpl(app)
    }


    @Provides
    @Singleton
    fun providePlantControlUseCases(repository: PlantControlRepository): PlantControlUseCases {
        return PlantControlUseCases(
            getPlantControls = GetPlantControls(repository),
            addPlantControl = AddPlantControl(repository),
            deletePlantControl = DeletePlantControls(repository),
            getPlantControl = GetPlantControl(repository),
            updateLocation = UpdatePlantControlLocation(repository),
            addIgnorePlantControl = AddIgnorePlantControl(repository),
        )
    }

    @Provides
    @Singleton
    fun provideVisitorContactUseCases(repository: VisitorContactRepository): VisitorContactUseCases {
        return VisitorContactUseCases(
            getVisitorContact = GetVisitorContact(repository),
            addVisitorContact = AddVisitorContact(repository),
            deleteVisitorContact = DeleteVisitorContacts(repository),
            getVisitorContacts = GetVisitorContacts(repository),
            updateLocation = UpdateVisitorContactLocation(
                repository
            ),
            addIgnoreVisitorContact = AddIgnoreVisitorContact(repository),

            )
    }

    @Provides
    @Singleton
    fun provideOtherObservationUseCases(repository: OtherObservationRepository): OtherObservationUseCases {
        return OtherObservationUseCases(
            getOtherObservation = GetOtherObservation(repository),
            addOtherObservation = AddOtherObservation(repository),
            addIgnoreOtherObservation = AddIgnoreOtherObservation(repository),
            deleteOtherObservations = DeleteOtherObservations(repository),
            getOtherObservations = GetOtherObservations(repository),
            updateLocation = UpdateObservationLocation(repository),
        )
    }

    @Provides
    @Singleton
    fun provideSpeciesReportUseCases(repository: SpeciesReportRepository): SpeciesReportUseCases {
        return SpeciesReportUseCases(
            getSpeciesReport = GetSpeciesReport(repository),
            addSpeciesReport = AddSpeciesReport(repository),
            addIgnoreSpeciesReport = AddIgnoreSpeciesReport(repository),
            deleteSpeciesReport = DeleteSpeciesReports(repository),
            getSpeciesReports = GetSpeciesReports(repository),
            updateLocation = UpdateSpeciesReportLocation(repository),
        )
    }

    @Provides
    @Singleton
    fun provideMonitoringUseCases(repository: MonitoringRepository): MonitoringUseCases {
        return MonitoringUseCases(
            getMonitoring = GetMonitoring(repository),
            addMonitoring = AddMonitoring(repository),
            addIgnoreMonitoring = AddIgnoreMonitoring(repository),
            deleteMonitoring = DeleteMonitoring(repository),
            getMonitorings = GetMonitorings(repository),
            updateLocation = UpdateMonitoringLocation(repository),
        )
    }

    @Provides
    @Singleton
    fun provideBiotopUseCases(repository: BiotopRepository): BiotopUseCases {
        return BiotopUseCases(
            getBiotop = GetBiotop(repository),
            addBiotop = AddBiotop(repository),
            addIgnoreBiotop = AddIgnoreBiotop(repository),
            deleteBiotops = DeleteBiotops(repository),
            getBiotops = GetBiotops(repository),
            updateLocation = UpdateBiotopLocation(repository),
        )
    }

    @Provides
    @Singleton
    fun provideMapEntryMarkerUseCases(repository: MapEntryMarkerRepository): MapEntryMarkerUseCases {
        return MapEntryMarkerUseCases(
            getMapEntryMarkers = GetMapEntryMarkers(repository)
        )
    }

    @Provides
    @Singleton
    fun provideFileStorageUseCases(repository: FileRepository): FileStorageUseCases {
        return FileStorageUseCases(
            save = SaveImage(repository),
            delete = Delete(repository),
            getBitmapFromUri = GetBitmapFromUri(repository),
            exportJson = ExportJson(repository),
            importJson = ImportJson(repository),
            saveTempUri = SaveTempUri(repository),
            exportCsv = ExportCsv(repository)
        )
    }
}




