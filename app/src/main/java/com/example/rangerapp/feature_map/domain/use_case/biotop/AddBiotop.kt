package com.example.rangerapp.feature_map.domain.use_case.biotop

import com.example.rangerapp.feature_map.domain.model.Biotop
import com.example.rangerapp.feature_map.domain.model.InvalidBiotopException
import com.example.rangerapp.feature_map.domain.model.InvalidSpeciesReportException
import com.example.rangerapp.feature_map.domain.repository.BiotopRepository

class AddBiotop(
    private val repository: BiotopRepository
) {

    @kotlin.jvm.Throws(InvalidBiotopException::class)
    suspend operator fun invoke(biotop: Biotop) {


        if (biotop.type.isEmpty()) {
            throw InvalidBiotopException("Das Feld Art darf nicht leer sein.")
        }

        repository.insert(biotop)


    }

}