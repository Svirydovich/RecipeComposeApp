package com.example.recipeapp.features.favorites.presentation.model

import com.example.recipeapp.features.recipes.presentation.model.RecipeUiModel

sealed interface FavoritesUiState {
    data object Loading : FavoritesUiState
    data class Error(val message: String) : FavoritesUiState
    data class Success(
        val recipes: List<RecipeUiModel>
    ) : FavoritesUiState

    data object Empty : FavoritesUiState
}
