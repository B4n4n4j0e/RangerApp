package com.example.rangerapp.feature_map.domain.use_case.other_observation

data class OtherObservationUseCases(
    val getOtherObservations: GetOtherObservations,
    val deleteOtherObservations: DeleteOtherObservations,
    val addOtherObservation: AddOtherObservation,
    val addIgnoreOtherObservation: AddIgnoreOtherObservation,
    val getOtherObservation: GetOtherObservation,
    val updateLocation: UpdateObservationLocation,
)
