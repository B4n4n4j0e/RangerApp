package com.example.rangerapp.feature_map.presentation.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CustomFormat {

    companion object {

        fun formatLocalDateTime(localDateTime: LocalDateTime): String {
            val zoneId = ZoneId.of("UTC")
            val zonedDateTime = localDateTime.atZone(zoneId)
            val adjustedDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
            return adjustedDateTime.format(formatter)
        }

        fun formatLocalDate(localDate: LocalDate): String {
            val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            return localDate.format(dateTimeFormatter)
        }

        fun formatLocalDateTimeShort(localDateTime: LocalDateTime): String {
            val zoneId = ZoneId.of("UTC")
            val zonedDateTime = localDateTime.atZone(zoneId)
            val adjustedDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            return adjustedDateTime.format(formatter)
        }
    }

}
