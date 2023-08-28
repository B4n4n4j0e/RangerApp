package com.example.rangerapp.feature_map.domain.use_case.visitor_contact

import com.example.rangerapp.feature_map.domain.model.VisitorContact
import com.example.rangerapp.feature_map.domain.repository.VisitorContactRepository
import java.time.LocalDate

class GetVisitorContacts(
    private val repository: VisitorContactRepository
) {

    suspend operator fun invoke(
        startCreatedDate: LocalDate?,
        endCreatedDate: LocalDate?,
        startUpdatedDate: LocalDate?,
        endUpdatedDate: LocalDate?,
        searchTerm: String

    ): List<VisitorContact> {
        return repository.getAll(
            startCreatedDate,
            endCreatedDate,
            startUpdatedDate,
            endUpdatedDate,
            searchTerm
        )
    }


}