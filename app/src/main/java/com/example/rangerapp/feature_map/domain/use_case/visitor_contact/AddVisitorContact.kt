package com.example.rangerapp.feature_map.domain.use_case.visitor_contact

import com.example.rangerapp.feature_map.domain.model.InvalidVisitorContactException
import com.example.rangerapp.feature_map.domain.model.VisitorContact
import com.example.rangerapp.feature_map.domain.repository.VisitorContactRepository

class AddVisitorContact(
    private val repository: VisitorContactRepository
) {

    @kotlin.jvm.Throws(InvalidVisitorContactException::class)
    suspend operator fun invoke(visitorContact: VisitorContact) {

        repository.insert(visitorContact)


    }

}