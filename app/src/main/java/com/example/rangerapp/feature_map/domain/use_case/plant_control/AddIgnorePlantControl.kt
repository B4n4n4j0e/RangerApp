package com.example.rangerapp.feature_map.domain.use_case.plant_control

import com.example.rangerapp.feature_map.domain.model.InvalidPlantControlException
import com.example.rangerapp.feature_map.domain.model.PlantControl
import com.example.rangerapp.feature_map.domain.repository.PlantControlRepository

class AddIgnorePlantControl(
    private val repository: PlantControlRepository
) {

    @kotlin.jvm.Throws(InvalidPlantControlException::class)
    suspend operator fun invoke(plantControl: PlantControl): Long {
        if (plantControl.imageUris.isEmpty()) {
            throw InvalidPlantControlException("Der Eintrag benötigt mindestens ein Bild")
        }
        return repository.insertIgnore(plantControl)


    }

}