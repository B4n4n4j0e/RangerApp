package com.example.rangerapp.feature_map.domain.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.json.JSONObject
import java.time.LocalDateTime


@Entity(
    tableName = "biotop",
    indices = [Index(
        value = ["created_timestamp", "longitude", "latitude"],
        unique = true
    )
    ]
)
data class Biotop(
    @PrimaryKey(autoGenerate = true) override val id: Int? = null,
    @ColumnInfo(name = "created_timestamp")
    override val createdTimestamp: LocalDateTime,
    @ColumnInfo(name = "last_updated_timestamp")
    override val lastUpdatedTimestamp: LocalDateTime,
    override val longitude: Double,
    override val latitude: Double,
    val type: String,
    val category: BiotopCategory,
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
        rootObject.put("biotop_type", type)
        rootObject.put("category", category)
        rootObject.put("type", MapEntryType.BIOTOP)
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
            type,
            category.value
        )
    }

    override fun getTitle(): String {
        return MapEntryType.BIOTOP.value + " - " + category.value + " - " + type + " - " + createdTimestamp.toString()
    }

    override fun getMapEntryType(): MapEntryType {
        return MapEntryType.BIOTOP
    }


    override fun uris(): List<Uri> {
        return imageUris
    }

    override fun importEquals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Biotop) return false
        return createdTimestamp == other.createdTimestamp
                && longitude == other.longitude
                && latitude == other.latitude
                && description == other.description
                && type == other.type
                && category == other.category
    }
}


enum class BiotopCategory(val value: String) {
    STREUOBST("Streuobst"),
    HALBTROCKENRASEN("Halbtrockenrasen"),
    TROCKENRASEN("Trockenrasen"),
    FEUCHTGEBIET("Feuchtgebiet"),
    HÖHLE("Höhle"),
    FELSLEBENSRAUM("Felslebensraum"),
    MÄHWIESE("Mähwiese"),
    GEWÄSSER("Gewässer"),
    MOOR("Moor"),
    WALD("Wald"),
    ANDERE("Andere")
}


fun getBiotopCategoryByName(value: String): BiotopCategory? {
    return BiotopCategory.values().find { it.value == value }
}

class InvalidBiotopException(message: String) : Exception(message)
