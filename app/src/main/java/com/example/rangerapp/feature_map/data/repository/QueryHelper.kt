package com.example.rangerapp.feature_map.data.repository

import java.time.LocalDate
import java.time.ZoneOffset

class QueryHelper {
    companion object {

        fun createQueryString(
            createdStartDate: LocalDate?,
            createdEndDate: LocalDate?,
            updatedStartDate: LocalDate?,
            updatedEndDate: LocalDate?,
            searchTerm: String,
            additionalLookUp: List<String>,
            startControlDate: LocalDate? = null,
            endControlDate: LocalDate? = null,
        ): String {
            val lookupList = listOf("description") + additionalLookUp
            val queryString = StringBuilder()

            if (createdStartDate != null || createdEndDate != null || updatedStartDate != null
                || updatedEndDate != null || searchTerm.isNotBlank() || startControlDate != null || endControlDate != null
            ) {


                queryString.append(" WHERE ")
                val conditions = mutableListOf<String>()
                val searchCondition = mutableListOf<String>()

                createdStartDate?.let {
                    conditions.add("created_timestamp >= ?")
                }
                createdEndDate?.let {
                    conditions.add("created_timestamp <= ?")

                }

                updatedStartDate?.let {
                    conditions.add("last_updated_timestamp >= ?")

                }
                updatedEndDate?.let {
                    conditions.add("last_updated_timestamp <= ?")

                }

                startControlDate?.let {
                    conditions.add("next_control_timestamp >= ?")
                }

                endControlDate?.let {
                    conditions.add("next_control_timestamp <= ?")

                }


                if (searchTerm.isNotBlank()) {
                    val updatedList = lookupList.drop(1)
                    if (updatedList.size == 0) {
                        conditions.add(" (${lookupList[0]} LIKE ? ) ")
                    } else {
                        conditions.add(" (${lookupList[0]} LIKE ? OR ")

                    }
                    for ((index, attribute) in updatedList.withIndex()) {
                        if (index == updatedList.lastIndex) {
                            searchCondition.add(" $attribute LIKE ? ) ")
                        } else {
                            searchCondition.add(" $attribute LIKE ? ")
                        }
                    }
                }

                queryString.append(conditions.joinToString(" AND "))
                queryString.append(searchCondition.joinToString(" OR "))
            }
            return queryString.toString()
        }

        fun createParamList(
            createdStartDate: LocalDate?,
            createdEndDate: LocalDate?,
            updatedStartDate: LocalDate?,
            updatedEndDate: LocalDate?,
            searchTerm: String,
            additionalLookUp: List<String>,
            controlStartDate: LocalDate? = null,
            controlEndDate: LocalDate? = null,

            ): List<String> {

            val lookupList = listOf("description") + additionalLookUp

            val queryParams = mutableListOf<String>()

            if (createdStartDate != null || createdEndDate != null || updatedStartDate != null
                || updatedEndDate != null || searchTerm.isNotBlank() || controlStartDate != null || controlEndDate != null
            ) {

                createdStartDate?.let {
                    queryParams.add(
                        it.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli().toString()
                    )
                }
                createdEndDate?.let {
                    queryParams.add(
                        it.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
                            .toString()
                    )
                }

                updatedStartDate?.let {
                    queryParams.add(
                        it.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli().toString()
                    )
                }
                updatedEndDate?.let {
                    queryParams.add(
                        it.plusDays(1L).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
                            .toString()
                    )
                }

                controlStartDate?.let {
                    queryParams.add(
                        it.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli().toString()
                    )
                }
                controlEndDate?.let {
                    queryParams.add(
                        it.plusDays(1L).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
                            .toString()
                    )
                }

                if (searchTerm.isNotBlank()) {
                    for (attribute in lookupList) {
                        queryParams.add("%$searchTerm%")
                    }
                }

            }

            return queryParams

        }
    }
}