package com.example.rangerapp.feature_map.domain.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.json.JSONObject
import java.time.LocalDateTime

@Entity(
    tableName = "other_observation",
    indices = [Index(
        value = ["created_timestamp", "longitude", "latitude"],
        unique = true
    )
    ]
)
data class OtherObservation(
    @PrimaryKey(autoGenerate = true) override val id: Int? = null,
    @ColumnInfo(name = "created_timestamp")
    override val createdTimestamp: LocalDateTime,
    @ColumnInfo(name = "last_updated_timestamp")
    override val lastUpdatedTimestamp: LocalDateTime,
    override val longitude: Double,
    override val latitude: Double,
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
        rootObject.put("type", MapEntryType.OTHER_OBSERVATION)
        return rootObject
    }

    override fun toCsvList(): List<String> {
        return listOf(
            id.toString(),
            description ?: "",
            createdTimestamp.toString(),
            lastUpdatedTimestamp.toString(),
            longitude.toString(),
            latitude.toString()
        )
    }

    override fun getTitle(): String {
        return MapEntryType.OTHER_OBSERVATION.value + " - " + createdTimestamp.toString()

    }

    override fun uris(): List<Uri> {
        return imageUris
    }

    override fun getMapEntryType(): MapEntryType {
        return MapEntryType.OTHER_OBSERVATION
    }

    override fun importEquals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OtherObservation) return false
        return createdTimestamp == other.createdTimestamp
                && longitude == other.longitude
                && latitude == other.latitude
                && description == other.description
    }
}


class InvalidOtherObservationException(message: String) : Exception(message)

