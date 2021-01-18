package com.example.steambacklog.database

import androidx.room.TypeConverter
import com.example.steambacklog.model.Completion

class Converters{
    @TypeConverter
    fun fromCompletion(value: String) = enumValueOf<Completion>(value)

    @TypeConverter
    fun toCompletion(value: Completion) = value.name
}