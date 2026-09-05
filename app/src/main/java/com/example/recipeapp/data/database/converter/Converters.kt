package com.example.recipeapp.data.database.converter

import androidx.room3.ColumnTypeConverter

class Converters {
    @ColumnTypeConverter
    fun fromString(value: String): List<String> {
        return value.split("|||")
    }

    @ColumnTypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString("|||")
    }
}
