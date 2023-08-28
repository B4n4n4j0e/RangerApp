package com.example.rangerapp.feature_map.domain.use_case.other_observation

import com.example.rangerapp.feature_map.domain.model.InvalidOtherObservationException
import com.example.rangerapp.feature_map.domain.model.OtherObservation
import com.example.rangerapp.feature_map.domain.repository.OtherObservationRepository

class AddIgnoreOtherObservation(
    private val repository: OtherObservationRepository
) {

    @kotlin.jvm.Throws(InvalidOtherObservationException::class)
    suspend operator fun invoke(otherObservation: OtherObservation): Long {
        return repository.insertIgnore(otherObservation)


    }

}