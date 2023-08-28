package com.example.rangerapp.feature_map.domain.use_case.plant_control

import com.example.rangerapp.feature_map.domain.model.PlantControl
import com.example.rangerapp.feature_map.domain.repository.PlantControlRepository
import java.time.LocalDate

class GetPlantControls(
    private val repository: PlantControlRepository
) {
    suspend operator fun invoke(
        startCreatedDate: LocalDate?,
        endCreatedDate: LocalDate?,
        startUpdatedDate: LocalDate?,
        endUpdatedDate: LocalDate?,
        searchTerm: String

    ): List<PlantControl> {
        return repository.getAll(
            startCreatedDate,
            endCreatedDate,
            startUpdatedDate,
            endUpdatedDate,
            searchTerm
        )
    }

}