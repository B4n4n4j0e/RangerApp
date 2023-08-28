package com.example.rangerapp.feature_map.domain.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.json.JSONObject
import java.time.LocalDateTime


@Entity(
    tableName = "monitoring",
    indices = [Index(
        value = ["created_timestamp", "longitude", "latitude"],
        unique = true
    )
    ]
)
data class Monitoring(
    @PrimaryKey(autoGenerate = true) override val id: Int? = null,
    @ColumnInfo(name = "created_timestamp")
    override val createdTimestamp: LocalDateTime,
    @ColumnInfo(name = "last_updated_timestamp")
    override val lastUpdatedTimestamp: LocalDateTime,
    override val longitude: Double,
    override val latitude: Double,
    val monitoringType: String?,
    val material: String?,
    val result: String?,
    @ColumnInfo(name = "next_control_timestamp")
    val nextControlDate: LocalDateTime?,
    override val description: String?,
    @ColumnInfo(name = "image_uris")
    val imageUris: List<Uri> = emptyList()

) : MapEntry() {
    override fun toJson(): JSONObject {
        val rootObject = JSONObject()
        rootObject.put("id", id)
        rootObject.put("created_timestamp", createdTimestamp)
        rootObject.put("last_updated_timestamp", lastUpdatedTimestamp)
        rootObject.put("next_control_date", nextControlDate)
        rootObject.put("longitude", longitude)
        rootObject.put("latitude", latitude)
        rootObject.put("description", description)
        rootObject.put("material", material)
        rootObject.put("monitoring_type", monitoringType)
        rootObject.put("result", result)
        rootObject.put("type", MapEntryType.MONITORING)
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
            material.toString(),
            monitoringType ?: "",
            nextControlDate.toString(),
            result.toString()
        )
    }

    override fun uris(): List<Uri> {
        return emptyList()
    }

    override fun getMapEntryType(): MapEntryType {
        return MapEntryType.MONITORING
    }

    override fun importEquals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Monitoring) return false
        return createdTimestamp == other.createdTimestamp
                && longitude == other.longitude
                && latitude == other.latitude
                && description == other.description
                && monitoringType == other.monitoringType
                && nextControlDate == other.nextControlDate
                && result == other.result
    }

    override fun getTitle(): String {
        return MapEntryType.MONITORING.value + " - " + nextControlDate.toString() + " - " + createdTimestamp.toString()
    }


}

class InvalidMonitoringException(message: String) : Exception(message)

