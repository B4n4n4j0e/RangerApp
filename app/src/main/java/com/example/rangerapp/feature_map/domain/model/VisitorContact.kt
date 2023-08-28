package com.example.rangerapp.feature_map.domain.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.json.JSONObject
import java.time.LocalDateTime


@Entity(
    tableName = "visitor_contact",
    indices = [Index(
        value = ["created_timestamp", "longitude", "latitude"],
        unique = true
    )
    ]
)
data class VisitorContact(
    @PrimaryKey(autoGenerate = true) override val id: Int? = null,
    @ColumnInfo(name = "created_timestamp")
    override val createdTimestamp: LocalDateTime,
    @ColumnInfo(name = "last_updated_timestamp")
    override val lastUpdatedTimestamp: LocalDateTime,
    override val longitude: Double,
    override val latitude: Double,
    val origin: String?,
    val violations: String?,
    val quantity: Int = 1,
    override val description: String?


) : MapEntry() {
    override fun toJson(): JSONObject {
        val rootObject = JSONObject()
        rootObject.put("id", id)
        rootObject.put("created_timestamp", createdTimestamp)
        rootObject.put("last_updated_timestamp", lastUpdatedTimestamp)
        rootObject.put("longitude", longitude)
        rootObject.put("latitude", latitude)
        rootObject.put("description", description)
        rootObject.put("origin", origin)
        rootObject.put("quantity", quantity)
        rootObject.put("violations", violations)
        rootObject.put("type", MapEntryType.VISITOR_CONTACT)

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
            origin ?: "",
            quantity.toString(),
            violations ?: ""
        )
    }

    override fun getTitle(): String {
        return MapEntryType.VISITOR_CONTACT.value + " - " + quantity.toString() + " - " + createdTimestamp.toString()
    }

    override fun uris(): List<Uri> {
        return emptyList()
    }

    override fun getMapEntryType(): MapEntryType {
        return MapEntryType.VISITOR_CONTACT
    }

    override fun importEquals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VisitorContact) return false
        return createdTimestamp == other.createdTimestamp
                && longitude == other.longitude
                && latitude == other.latitude
                && description == other.description
                && violations == other.violations
                && origin == other.origin
                && quantity == other.quantity
    }
}

class InvalidVisitorContactException(message: String) : Exception(message)

