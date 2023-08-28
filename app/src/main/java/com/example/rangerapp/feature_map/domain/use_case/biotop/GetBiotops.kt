package com.example.rangerapp.feature_map.domain.use_case.biotop

import com.example.rangerapp.feature_map.domain.model.Biotop
import com.example.rangerapp.feature_map.domain.repository.BiotopRepository
import java.time.LocalDate

class GetBiotops(
    private val repository: BiotopRepository
) {
    suspend operator fun invoke(
        startCreatedDate: LocalDate?,
        endCreatedDate: LocalDate?,
        startUpdatedDate: LocalDate?,
        endUpdatedDate: LocalDate?,
        searchTerm: String

    ): List<Biotop> {
        return repository.getAll(
            startCreatedDate,
            endCreatedDate,
            startUpdatedDate,
            endUpdatedDate,
            searchTerm
        )
    }

}