package com.example.rangerapp.feature_map.domain.use_case.plant_control

import com.example.rangerapp.feature_map.domain.model.PlantControl
import com.example.rangerapp.feature_map.domain.repository.PlantControlRepository
import java.time.LocalDateTime

class GetPlantControl(
    private val repository: PlantControlRepository
) {
    suspend operator fun invoke(id: Int): PlantControl? {
        return repository.getById(id)
    }

    suspend operator fun invoke(
        createdTimestamp: LocalDateTime,
        latitude: Double,
        longitude: Double
    ): PlantControl? {
        return repository.getByIndex(createdTimestamp, latitude, longitude)
    }
}