package com.example.rangerapp.feature_map.domain.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.json.JSONObject
import java.time.LocalDateTime


@Entity(
    tableName = "species_report",
    indices = [Index(
        value = ["created_timestamp", "longitude", "latitude"],
        unique = true
    )
    ]
)
data class SpeciesReport(
    @PrimaryKey(autoGenerate = true) override val id: Int? = null,
    @ColumnInfo(name = "created_timestamp")
    override val createdTimestamp: LocalDateTime,
    @ColumnInfo(name = "last_updated_timestamp")
    override val lastUpdatedTimestamp: LocalDateTime,
    override val longitude: Double,
    override val latitude: Double,
    val species: String,
    val quantity: Int = 1,
    val category: SpeciesCategory,
    override val description: String?,
    @ColumnInfo(name = "image_uris")
    val imageUris: List<Uri> = emptyList()

) : MapEntry() {
    override fun toJson(): JSONObject {
        val rootObject = JSONObject()
        rootObject.put("id", id)
        rootObject.put("created_timestamp", createdTimestamp)
        rootObject.put("last_updated_timestamp", lastUpdatedTimestamp)
        rootObject.put("longitude", longitude)
        rootObject.put("latitude", latitude)
        rootObject.put("description", description)
        rootObject.put("species", species)
        rootObject.put("quantity", quantity)
        rootObject.put("category", category)
        rootObject.put("type", MapEntryType.SPECIES_REPORT)

        return rootObject
    }

    override fun toCsvList(): List<String> {
        return listOf(
            id.toString(),
            description ?: "",
            createdTimestamp.toString(),
            lastUpdatedTimestamp.toString(),
            longitude.toString(),
            latitude.toString(),
            species,
            quantity.toString(),
            category.value
        )
    }

    override fun getTitle(): String {
        return MapEntryType.SPECIES_REPORT.value + " - " + category.value + " - " + species + " - " + quantity + " - " + createdTimestamp.toString()
    }

    override fun getMapEntryType(): MapEntryType {
        return MapEntryType.SPECIES_REPORT
    }


    override fun uris(): List<Uri> {
        return imageUris
    }

    override fun importEquals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SpeciesReport) return false
        return createdTimestamp == other.createdTimestamp
                && longitude == other.longitude
                && latitude == other.latitude
                && description == other.description
                && species == other.species
                && category == other.category
                && quantity == other.quantity
    }
}


enum class SpeciesCategory(val value: String) {
    PFLANZE("Pflanze"),
    VOGEL("Vogel"),
    SÄUGETIER("Säugetier"),
    INSEKT("Insekt"),
    AMPHIBIE("Amphibie"),
    REPTILIE("Reptilie"),
    ANDERE("Andere")
}


fun getSpeciesCategoryByName(value: String): SpeciesCategory? {
    return SpeciesCategory.values().find { it.value == value }
}

class InvalidSpeciesReportException(message: String) : Exception(message)
