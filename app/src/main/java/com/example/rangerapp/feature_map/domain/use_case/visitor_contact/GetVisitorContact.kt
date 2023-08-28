package com.example.rangerapp.feature_map.domain.use_case.visitor_contact

import com.example.rangerapp.feature_map.domain.model.VisitorContact
import com.example.rangerapp.feature_map.domain.repository.VisitorContactRepository
import java.time.LocalDateTime

class GetVisitorContact(
    private val repository: VisitorContactRepository
) {
    suspend operator fun invoke(id: Int): VisitorContact? {
        return repository.getById(id)
    }

    suspend operator fun invoke(
        createdTimestamp: LocalDateTime,
        latitude: Double,
        longitude: Double
    ): VisitorContact? {
        return repository.getByIndex(createdTimestamp, latitude, longitude)
    }


}