package com.example.recipeapp.features.recipes.presentation.model

import androidx.compose.runtime.Immutable

@Immutable
data class RecipesUiState(
    val recipes: List<RecipeUiModel> = emptyList(),
    val categoryTitle: String = "",
    val categoryImageUrl: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isInitialLoading: Boolean
        get() = isLoading && recipes.isEmpty()

    val isEmpty: Boolean
        get() = !isLoading && recipes.isEmpty() && error == null

    val hasRecipes: Boolean
        get() = recipes.isNotEmpty()

    val hasError: Boolean
        get() = error != null && recipes.isEmpty()
}
