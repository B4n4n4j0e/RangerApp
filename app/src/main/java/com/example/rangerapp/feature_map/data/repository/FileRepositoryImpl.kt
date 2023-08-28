package com.example.rangerapp.feature_map.data.repository

import MapEntryDeserializer
import android.app.Application
import android.app.RecoverableSecurityException
import android.content.IntentSender
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.example.rangerapp.feature_map.data.util.StorageFunctions.Companion.contentValues
import com.example.rangerapp.feature_map.data.util.StorageFunctions.Companion.saveImage
import com.example.rangerapp.feature_map.domain.model.Biotop
import com.example.rangerapp.feature_map.domain.model.MapEntry
import com.example.rangerapp.feature_map.domain.model.Monitoring
import com.example.rangerapp.feature_map.domain.model.OtherObservation
import com.example.rangerapp.feature_map.domain.model.PlantControl
import com.example.rangerapp.feature_map.domain.model.SpeciesReport
import com.example.rangerapp.feature_map.domain.model.VisitorContact
import com.example.rangerapp.feature_map.domain.repository.FileRepository
import com.example.rangerapp.feature_map.presentation.util.sdk29AndUp
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.Locale
import java.util.UUID

class FileRepositoryImpl(
    private val appContext: Application
) : FileRepository {
    override fun saveTempUri(uri: Uri): Uri? {
        val imageCollection = sdk29AndUp {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val uuid = UUID.randomUUID().toString()
        val fileName = "IMG_$timeStamp$uuid.jpg"
        val content = contentValues(fileName)
        return try {
            appContext.contentResolver.insert(imageCollection, content)?.also { newUri ->
                appContext.contentResolver.openInputStream(uri)?.use { inputStream ->
                    appContext.contentResolver.openOutputStream(newUri)?.use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                delete(uri)
                return newUri.normalizeScheme()
            } ?: throw IOException("Couldn't create MediaStore entry")
        } catch (e: IOException) {
            throw RangerAppFrontendException("MediaStore Eintrag konnte nicht erstellt werden.")

        }
    }

    override fun saveImage(bitmap: Bitmap): Uri? {
        return saveImage(bitmap, appContext)

    }


    override fun delete(uri: Uri): IntentSender? {
        try {
            appContext.contentResolver.delete(uri, null, null)
            return null
        } catch (e: SecurityException) {
            val intentSender = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                    MediaStore.createDeleteRequest(
                        appContext.contentResolver,
                        listOf(uri)
                    ).intentSender
                }

                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {

                    val recoverableSecurityException = e as? RecoverableSecurityException
                    recoverableSecurityException?.userAction?.actionIntent?.intentSender
                }

                else -> null
            }
            return intentSender
        }
    }

    override fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream = appContext.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap
        } catch (e: IOException) {
            throw RangerAppFrontendException("Bild konnte nicht gefunden wurden.")
        }
    }

    override fun exportJson(mapEntries: List<MapEntry>, withPicture: Boolean): Uri? {
        val fileName = "export.json"
        val file = File(appContext.filesDir, fileName)
        try {

            BufferedWriter(FileWriter(file)).use { bufferedWriter ->
                bufferedWriter.write("[")
                for ((index, mapEntry) in mapEntries.withIndex()) {

                    val existingUris = mapEntry.uris().filter { uri ->
                        uriExists(uri)
                    }
                    if (mapEntry.getMapEntryType() == MapEntry.MapEntryType.PLANT_CONTROL && (existingUris.isEmpty() || !withPicture)) {
                        continue
                    }

                    var jsonString = mapEntry.toJson().toString()
                    jsonString = jsonString.substring(0, jsonString.length - 1)
                    bufferedWriter.write(jsonString)

                    if (existingUris.isNotEmpty() && withPicture) {
                        bufferedWriter.write(",")
                        bufferedWriter.write("\"images\":[")
                        for (i in existingUris.indices) {
                            val uriFileName = getFileName(existingUris[i])
                            bufferedWriter.write("{\"name\": \"$uriFileName\",")
                            bufferedWriter.write("\"data\":\"")
                            encodeToBase64(existingUris[i], bufferedWriter)
                            bufferedWriter.write("\"}")
                            if (i < existingUris.size - 1) {
                                bufferedWriter.write(",")
                            }
                        }
                        bufferedWriter.write("]")
                    }
                    if (index == mapEntries.lastIndex) {
                        bufferedWriter.write("}")
                    } else {
                        bufferedWriter.write("},")
                    }
                }
                bufferedWriter.write("]")
                bufferedWriter.newLine()
                return FileProvider.getUriForFile(
                    appContext,
                    appContext.applicationContext.packageName + ".provider",
                    file
                );
            }
        } catch (e: IOException) {
            throw RangerAppFrontendException("Export fehlgeschlagen. Bitte versuchen Sie es erneut.")
        }
    }

    override fun exportCsv(mapEntries: List<MapEntry>, mapEntryType: MapEntry.MapEntryType): Uri? {
        val fileName = when (mapEntryType) {
            MapEntry.MapEntryType.PLANT_CONTROL -> "plant_control.csv"
            MapEntry.MapEntryType.SPECIES_REPORT -> "species_report.csv"
            MapEntry.MapEntryType.VISITOR_CONTACT -> "visitor_contact.csv"
            MapEntry.MapEntryType.OTHER_OBSERVATION -> "other_observation.csv"
            MapEntry.MapEntryType.MONITORING -> "monitoring.csv"
            MapEntry.MapEntryType.BIOTOP -> "biotop.csv"
        }
        val file = File(appContext.filesDir, fileName)
        try {
            BufferedWriter(FileWriter(file)).use { bufferedWriter ->
                val csvPrinter = CSVPrinter(bufferedWriter, CSVFormat.DEFAULT)
                when (mapEntryType) {
                    MapEntry.MapEntryType.PLANT_CONTROL -> {
                        csvPrinter.printRecord(
                            listOf(
                                "Id",
                                "Beschreibung",
                                "Erstellt am",
                                "Zuletzt verändert",
                                "Längengrad",
                                "Breitengrad",
                            )
                        )
                        mapEntries.forEach {
                            val plantControl = it as PlantControl
                            csvPrinter.printRecord(plantControl.toCsvList())
                        }
                    }

                    MapEntry.MapEntryType.SPECIES_REPORT -> {
                        csvPrinter.printRecord(
                            listOf(
                                "Id",
                                "Beschreibung",
                                "Erstellt am",
                                "Zuletzt verändert",
                                "Längengrad",
                                "Breitengrad",
                                "Art",
                                "Anzahl",
                                "Kategorie"
                            )
                        )
                        mapEntries.forEach {
                            val speciesReport = it as SpeciesReport
                            csvPrinter.printRecord(speciesReport.toCsvList())
                        }
                    }

                    MapEntry.MapEntryType.VISITOR_CONTACT -> {
                        csvPrinter.printRecord(
                            listOf(
                                "Id",
                                "Beschreibung",
                                "Erstellt am",
                                "Zuletzt verändert",
                                "Längengrad",
                                "Breitengrad",
                                "Herkunft",
                                "Anzahl",
                                "Verstöße"
                            )
                        )
                        mapEntries.forEach {
                            val visitorContact = it as VisitorContact
                            csvPrinter.printRecord(visitorContact.toCsvList())
                        }
                    }

                    MapEntry.MapEntryType.OTHER_OBSERVATION -> {
                        csvPrinter.printRecord(
                            listOf(
                                "Id",
                                "Beschreibung",
                                "Erstellt am",
                                "Zuletzt verändert",
                                "Längengrad",
                                "Breitengrad",
                            )
                        )
                        mapEntries.forEach {
                            val otherObservation = it as OtherObservation
                            csvPrinter.printRecord(otherObservation.toCsvList())
                        }
                    }

                    MapEntry.MapEntryType.MONITORING -> {
                        csvPrinter.printRecord(
                            listOf(
                                "Id",
                                "Beschreibung",
                                "Erstellt am",
                                "Zuletzt verändert",
                                "Längengrad",
                                "Breitengrad",
                                "Material",
                                "Monitoring Typ",
                                "Nächstes Kontrolle am",
                                "Ergebnis"
                            )
                        )
                        mapEntries.forEach {
                            val monitoring = it as Monitoring
                            csvPrinter.printRecord(monitoring.toCsvList())
                        }
                    }

                    MapEntry.MapEntryType.BIOTOP -> {
                        csvPrinter.printRecord(
                            listOf(
                                "Id",
                                "Beschreibung",
                                "Erstellt am",
                                "Zuletzt verändert",
                                "Längengrad",
                                "Breitengrad",
                                "Biotop Beschreibung",
                                "Kategorie"
                            )
                        )
                        mapEntries.forEach {
                            val biotop = it as Biotop
                            csvPrinter.printRecord(biotop.toCsvList())
                        }
                    }
                }
                csvPrinter.flush()
                return FileProvider.getUriForFile(
                    appContext,
                    appContext.applicationContext.packageName + ".provider",
                    file
                );
            }
        } catch (e: IOException) {
            throw RangerAppFrontendException("Export fehlgeschlagen. Bitte versuchen Sie es erneut.")
        }
    }


    override fun importJson(uri: Uri): List<MapEntry> {
        val gson = GsonBuilder()
            .registerTypeAdapter(MapEntry::class.java, MapEntryDeserializer(appContext))
            .create()
        val inputStream = appContext.contentResolver.openInputStream(uri)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val listType = object : TypeToken<List<MapEntry>>() {}.type
        try {
            return gson.fromJson(reader, listType)
        } catch (e: JsonSyntaxException) {
            throw RangerAppFrontendException("JSON Syntax Fehler. Bitte überprüfen Sie die JSON-Datei auf Syntaxfehler. 0 Einträge wurden importiert.")
        }

    }


    private fun getFileName(uri: Uri): String? {
        var fileName: String? = null
        val cursor: Cursor? = appContext.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex: Int = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    fileName = it.getString(displayNameIndex)
                }
            }
        }
        cursor?.close()
        return fileName
    }

    private fun encodeToBase64(uri: Uri, writer: BufferedWriter) {

        appContext.contentResolver.openInputStream(uri)?.use { inputStream ->
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            val base64Image = Base64.getEncoder().encodeToString(buffer)
            writer.write(base64Image)

        }
    }

    private fun uriExists(uri: Uri): Boolean {
        return try {
            appContext.contentResolver.openInputStream(uri)?.use {
                true
            } ?: false
        } catch (e: IOException) {
            false
        }
    }
}
