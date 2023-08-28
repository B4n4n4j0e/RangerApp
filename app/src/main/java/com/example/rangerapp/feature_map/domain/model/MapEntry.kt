package com.example.rangerapp.feature_map.domain.model

import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.example.rangerapp.ui.theme.ThemeBlue
import com.example.rangerapp.ui.theme.ThemeBrown
import com.example.rangerapp.ui.theme.ThemeDarkBlue
import com.example.rangerapp.ui.theme.ThemeGreen
import com.example.rangerapp.ui.theme.ThemeOrange
import com.example.rangerapp.ui.theme.ThemeRed
import org.json.JSONObject
import java.time.LocalDateTime


abstract class MapEntry {
    abstract val createdTimestamp: LocalDateTime
    abstract val lastUpdatedTimestamp: LocalDateTime
    abstract val longitude: Double
    abstract val latitude: Double
    abstract val id: Int?
    abstract val description: String?
    abstract fun toJson(): JSONObject
    abstract fun toCsvList(): List<String>


    abstract fun getTitle(): String

    abstract fun uris(): List<Uri>
    abstract fun getMapEntryType(): MapEntryType
    abstract fun importEquals(other: Any?): Boolean


    enum class MapEntryType(
        val value: String,
        val color: Color,
        val table_name: String,
        val title: String,
        val search_attributes: List<String>
    ) {
        SPECIES_REPORT(
            "Art Meldung",
            ThemeGreen,
            "species_report",
            "(\"Artmeldung - \" || category || \" - \" || species || \" - \" )",
            listOf("species", "category")
        ),
        VISITOR_CONTACT(
            "Besucherkontakt",
            ThemeOrange,
            "visitor_contact",
            "(\"Besucherkontakt - \" || quantity || \" - \" )",
            listOf("origin", "violations")
        ),
        PLANT_CONTROL(
            "Anlagenkontrolle",
            ThemeBrown,
            "plant_control",
            "(\"Anlagenkontrolle - \")",
            emptyList()
        ),
        OTHER_OBSERVATION(
            "Sonstige Beobachtungen",
            ThemeBlue,
            "other_observation",
            "(\"Sonstige Beobachtungen - \")",
            emptyList()
        ),
        MONITORING(
            "Monitoring",
            ThemeRed,
            "monitoring",
            "(\"Monitoring - \")",
            listOf("result", "material", "monitoringType")
        ),
        BIOTOP(
            "Biotop",
            ThemeDarkBlue,
            "biotop",
            "(\"Biotop - \" || category || \" - \" || type || \" - \" )",
            listOf("category", "type")
        )

    }


}

