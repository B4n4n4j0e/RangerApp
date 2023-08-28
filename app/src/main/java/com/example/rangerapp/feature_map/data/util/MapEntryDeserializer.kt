import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.example.rangerapp.feature_map.data.repository.RangerAppFrontendException
import com.example.rangerapp.feature_map.data.util.StorageFunctions.Companion.saveImage
import com.example.rangerapp.feature_map.domain.model.Biotop
import com.example.rangerapp.feature_map.domain.model.BiotopCategory
import com.example.rangerapp.feature_map.domain.model.MapEntry
import com.example.rangerapp.feature_map.domain.model.Monitoring
import com.example.rangerapp.feature_map.domain.model.OtherObservation
import com.example.rangerapp.feature_map.domain.model.PlantControl
import com.example.rangerapp.feature_map.domain.model.SpeciesCategory
import com.example.rangerapp.feature_map.domain.model.SpeciesReport
import com.example.rangerapp.feature_map.domain.model.VisitorContact
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.ZoneOffset

class MapEntryDeserializer(

    private val appContext: Context,
) : JsonDeserializer<MapEntry> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): MapEntry? {
        val jsonObject = json.asJsonObject

        val createdTimestamp = LocalDateTime.parse(jsonObject.get("created_timestamp").asString)
        try {
            val id = jsonObject.get("id").asInt
            val longitude = jsonObject.get("longitude").asDouble
            val latitude = jsonObject.get("latitude").asDouble


            val type = jsonObject.get("type").asString
            val mapEntryType = MapEntry.MapEntryType.valueOf(type)
            val description = jsonObject.get("description").asString

            val imageArray = jsonObject.getAsJsonArray("images")
            val imageUrls = mutableListOf<Uri>()

            imageArray?.forEach { imageElement ->
                val imageObject = imageElement.asJsonObject
                val fileName = imageObject.get("name").asString
                val data = imageObject.get("data").asString
                val bitmap = base64ImageToBitMap(data)
                val uri = saveImage(bitmap, appContext, fileName)
                uri?.let { url -> imageUrls.add(url) }
            }

            val updatedTimestamp = LocalDateTime.now(ZoneOffset.UTC)


            when (mapEntryType) {
                MapEntry.MapEntryType.SPECIES_REPORT -> {

                    val quantity = jsonObject.get("quantity").asInt
                    val species = jsonObject.get("species").asString
                    val category = jsonObject.get("category").asString
                    val speciesCategory = try {
                        SpeciesCategory.valueOf(category)
                    } catch (e: IllegalArgumentException) {
                        SpeciesCategory.ANDERE
                    }

                    return SpeciesReport(
                        id = null,
                        description = description,
                        createdTimestamp = createdTimestamp,
                        lastUpdatedTimestamp = updatedTimestamp,
                        longitude = longitude,
                        latitude = latitude,
                        category = speciesCategory,
                        quantity = quantity,
                        species = species,
                        imageUris = imageUrls
                    )
                }

                MapEntry.MapEntryType.VISITOR_CONTACT -> {
                    val quantity = jsonObject.get("quantity").asInt
                    val violations = jsonObject.get("violations").asString
                    val origin = jsonObject.get("origin").asString

                    return VisitorContact(
                        id = null,
                        description = description,
                        createdTimestamp = createdTimestamp,
                        lastUpdatedTimestamp = updatedTimestamp,
                        longitude = longitude,
                        latitude = latitude,
                        violations = violations,
                        quantity = quantity,
                        origin = origin,
                    )
                }

                MapEntry.MapEntryType.PLANT_CONTROL -> {
                    return PlantControl(
                        id = null,
                        description = description,
                        createdTimestamp = createdTimestamp,
                        lastUpdatedTimestamp = updatedTimestamp,
                        longitude = longitude,
                        latitude = latitude,
                        imageUris = imageUrls
                    )
                }

                MapEntry.MapEntryType.OTHER_OBSERVATION -> {
                    return OtherObservation(
                        id = null,
                        description = description,
                        createdTimestamp = createdTimestamp,
                        lastUpdatedTimestamp = updatedTimestamp,
                        longitude = longitude,
                        latitude = latitude,
                        imageUris = imageUrls
                    )
                }

                MapEntry.MapEntryType.MONITORING -> {
                    val material = jsonObject.get("material").asString
                    val monitoringType = jsonObject.get("monitoring_type").asString

                    val nextControlDate = try {
                        LocalDateTime.parse(jsonObject.get("next_control_date").asString)
                    } catch (e: NullPointerException) {
                        null
                    }
                    val result = jsonObject.get("result").asString

                    return Monitoring(
                        id = null,
                        description = description,
                        createdTimestamp = createdTimestamp,
                        lastUpdatedTimestamp = updatedTimestamp,
                        longitude = longitude,
                        latitude = latitude,
                        imageUris = imageUrls,
                        material = material,
                        monitoringType = monitoringType,
                        nextControlDate = nextControlDate,
                        result = result
                    )

                }

                MapEntry.MapEntryType.BIOTOP -> {

                    val category = jsonObject.get("category").asString
                    val biotop_type = jsonObject.get("biotop_type").asString
                    val biotopCategory = try {
                        BiotopCategory.valueOf(category)
                    } catch (e: IllegalArgumentException) {
                        BiotopCategory.ANDERE
                    }
                    val biotop = Biotop(
                        id = null,
                        description = description,
                        createdTimestamp = createdTimestamp,
                        lastUpdatedTimestamp = updatedTimestamp,
                        longitude = longitude,
                        latitude = latitude,
                        imageUris = imageUrls,
                        category = biotopCategory,
                        type = biotop_type
                    )
                    return biotop
                }
            }
        } catch (e: NullPointerException) {
            throw RangerAppFrontendException("Eintrag mit Erstellungszeitstempel $createdTimestamp ungültig. Bitte überprüfen Sie, ob alle Angaben korrekt sind.")
        } catch (e: java.lang.NumberFormatException) {
            throw RangerAppFrontendException("Eintrag mit Erstellungszeitstempel $createdTimestamp ungültig. Bitte überprüfen Sie, ob alle Angaben korrekt sind.")
        }

    }


    private fun base64ImageToBitMap(base64String: String): Bitmap {
        try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: java.lang.IllegalArgumentException) {
            throw RangerAppFrontendException("Die Base64-Kodierung eines Bildes ist fehlerhaft. Importvorgang abgebrochen.")
        }
    }


}