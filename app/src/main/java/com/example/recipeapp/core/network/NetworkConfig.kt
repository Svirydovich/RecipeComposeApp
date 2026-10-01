package com.example.recipeapp.core.network

import kotlinx.serialization.json.Json

object NetworkConfig {
    const val BASE_URL = "https://recipes.androidsprint.ru/api/"

    val json: Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
}
