package com.example.rangerapp.feature_map.domain.use_case.plant_control


data class PlantControlUseCases(
    val addPlantControl: AddPlantControl,
    val addIgnorePlantControl: AddIgnorePlantControl,
    val getPlantControl: GetPlantControl,
    val getPlantControls: GetPlantControls,
    val deletePlantControl: DeletePlantControls,
    val updateLocation: UpdatePlantControlLocation,
)

