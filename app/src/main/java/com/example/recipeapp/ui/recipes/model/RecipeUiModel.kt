package com.example.recipeapp.ui.recipes.model

import androidx.compose.runtime.Immutable
import com.example.recipeapp.Constants
import com.example.recipeapp.data.model.RecipeDto

@Immutable
data class RecipeUiModel(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val ingredients: List<IngredientUiModel>,
    val method: List<String>,
    val isFavorite: Boolean
)

fun RecipeDto.toUiModel() = RecipeUiModel(
    id = id,
    title = title,
    imageUrl = if (imageUrl == null) ""
    else if (imageUrl.startsWith("http")) imageUrl
    else "${Constants.ASSETS_URI_PREFIX}$imageUrl",
    ingredients = ingredients.map { it.toUiModel() },
    method = method,
    isFavorite = false
)
