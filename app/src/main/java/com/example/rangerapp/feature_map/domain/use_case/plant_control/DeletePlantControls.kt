package com.example.rangerapp.feature_map.domain.use_case.plant_control

import com.example.rangerapp.feature_map.domain.model.PlantControl
import com.example.rangerapp.feature_map.domain.repository.PlantControlRepository

class DeletePlantControls(
    private val repository: PlantControlRepository
) {

    suspend operator fun invoke(plantControls: List<PlantControl>) {
        repository.delete(plantControls)
    }

}