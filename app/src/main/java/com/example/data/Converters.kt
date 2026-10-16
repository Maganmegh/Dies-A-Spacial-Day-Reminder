package com.example.data

import androidx.room.TypeConverter
import com.example.model.EventType
import com.example.model.RelationshipType
import org.json.JSONArray

class Converters {
    @TypeConverter
    fun fromEventType(value: EventType): String = value.name

    @TypeConverter
    fun toEventType(value: String): EventType = try {
        EventType.valueOf(value)
    } catch (_: Exception) {
        EventType.BIRTHDAY
    }

    @TypeConverter
    fun fromRelationshipType(value: RelationshipType): String = value.name

    @TypeConverter
    fun toRelationshipType(value: String): RelationshipType = try {
        RelationshipType.valueOf(value)
    } catch (_: Exception) {
        RelationshipType.FRIEND
    }

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        val jsonArray = JSONArray()
        list.forEach { jsonArray.put(it) }
        return jsonArray.toString()
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return try {
            val jsonArray = JSONArray(value)
            val list = mutableListOf<String>()
            for (i in 0 until jsonArray.length()) {
                list.add(jsonArray.getString(i))
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }
}
