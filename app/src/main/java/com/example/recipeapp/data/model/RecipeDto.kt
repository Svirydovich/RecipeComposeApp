package com.example.recipeapp.data.model

import android.util.Log
import com.example.recipeapp.data.database.entity.RecipeEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

@Serializable
data class RecipeDto(
    val id: Int,
    val title: String,
    val ingredients: List<IngredientDto>,
    val method: List<String>,
    val imageUrl: String?,
    val servings: Int
)

private val recipeJson = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

fun RecipeDto.toEntity(categoryId: Int) = RecipeEntity(
    id = id,
    title = title,
    categoryId = categoryId,
    imageUrl = imageUrl ?: "",
    ingredients = recipeJson.encodeToString(ingredients),
    method = recipeJson.encodeToString(method),
    servings = servings
)

fun RecipeEntity.toDto() = RecipeDto(
    id = id,
    title = title,
    ingredients = runCatching { recipeJson.decodeFromString<List<IngredientDto>>(ingredients) }
        .onFailure { exception ->
            Log.e(
                "RecipeMapper",
                "Failed to parse ingredients for recipeId=$id. Raw data: $ingredients",
                exception
            )
        }
        .getOrDefault(
            emptyList()
        ),
    method = runCatching { recipeJson.decodeFromString<List<String>>(method) }
        .onFailure { exception ->
            Log.e(
                "RecipeMapper",
                "Failed to parse method for recipeId=$id. Raw data: $method",
                exception
            )
        }
        .getOrDefault(
            emptyList()
        ),
    imageUrl = imageUrl.ifBlank { null },
    servings = servings ?: 1
)
