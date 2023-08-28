package com.example.rangerapp.feature_map.domain.use_case.other_observation

import com.example.rangerapp.feature_map.domain.model.OtherObservation
import com.example.rangerapp.feature_map.domain.repository.OtherObservationRepository
import java.time.LocalDate

class GetOtherObservations(
    private val repository: OtherObservationRepository
) {

    suspend operator fun invoke(
        startCreatedDate: LocalDate?,
        endCreatedDate: LocalDate?,
        startUpdatedDate: LocalDate?,
        endUpdatedDate: LocalDate?,
        searchTerm: String

    ): List<OtherObservation> {
        return repository.getAll(
            startCreatedDate,
            endCreatedDate,
            startUpdatedDate,
            endUpdatedDate,
            searchTerm
        )
    }

}