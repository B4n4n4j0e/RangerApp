package com.example.rangerapp.feature_map.domain.use_case.visitor_contact

import com.example.rangerapp.feature_map.domain.model.VisitorContact
import com.example.rangerapp.feature_map.domain.repository.VisitorContactRepository

class DeleteVisitorContacts(
    private val repository: VisitorContactRepository
) {

    suspend operator fun invoke(visitorContacts: List<VisitorContact>) {
        repository.delete(visitorContacts)
    }

}