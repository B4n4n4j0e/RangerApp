package com.example.rangerapp.feature_map.domain.util

import android.net.Uri
import android.util.Log
import androidx.room.TypeConverter
import com.example.rangerapp.feature_map.domain.model.BiotopCategory
import com.example.rangerapp.feature_map.domain.model.SpeciesCategory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class Converters {
    private val gson: Gson = Gson()
    private val uriType: Type = object : TypeToken<List<String>>() {}.type
    

    @TypeConverter
    fun fromLocalDateTime(value: Long?): LocalDateTime? {
        return value?.let {
            val instant = Instant.ofEpochMilli(it)
            return LocalDateTime.ofInstant(instant, ZoneId.of("UTC"))
        }
    }

    @TypeConverter
    fun longToLocalDateTime(date: LocalDateTime?): Long? {
        return date?.let {
            val instant = it.atZone(ZoneId.of("UTC")).toInstant()
            return instant.toEpochMilli()
        }
    }

    @TypeConverter
    fun fromJsonToUris(value: String): List<Uri> {
        val uriList: List<String> = gson.fromJson(value, uriType)
        return uriList.map { Uri.parse(it) }
    }

    @TypeConverter
    fun urisToString(list: List<Uri>): String {

        val uriList = list.map {
            it.toString()
        }
        return gson.toJson(uriList, uriType)
    }

}