package com.dopayurii.movie.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Room TypeConverter for converting between List<String> and JSON string.
 * Used to persist list fields (genre, actors, directors, etc.) in the database.
 */
class StringListConverter {
    private val gson = Gson()

    /**
     * Converts a List<String> to a JSON string for database storage.
     *
     * @param list List of strings to convert
     * @return JSON string representation of the list
     */
    @TypeConverter
    fun fromList(list: List<String>): String {
        return gson.toJson(list)
    }

    /**
     * Converts a JSON string back to a List<String>.
     *
     * @param json JSON string from database
     * @return List of strings, or empty list if json is empty
     */
    @TypeConverter
    fun toList(json: String): List<String> {
        return if (json.isEmpty()) {
                emptyList()
            } else {
               gson.fromJson(json, object : TypeToken<List<String>>() {}.type)
            }
    }
}
