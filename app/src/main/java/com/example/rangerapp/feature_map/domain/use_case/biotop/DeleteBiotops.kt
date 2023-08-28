package com.example.rangerapp.feature_map.domain.use_case.biotop

import com.example.rangerapp.feature_map.domain.model.Biotop
import com.example.rangerapp.feature_map.domain.repository.BiotopRepository

class DeleteBiotops(
    private val repository: BiotopRepository
) {

    suspend operator fun invoke(biotopList: List<Biotop>) {
        repository.delete(biotopList)
    }

}