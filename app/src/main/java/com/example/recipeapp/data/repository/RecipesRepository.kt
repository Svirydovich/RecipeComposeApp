package com.example.recipeapp.data.repository

import com.example.recipeapp.core.util.FavoriteDataStoreManager
import com.example.recipeapp.data.model.RecipeDto
import com.example.recipeapp.features.recipes.presentation.model.RecipeUiModel
import com.example.recipeapp.features.recipes.presentation.model.toUiModel
import kotlinx.coroutines.flow.firstOrNull

class RecipesRepository(private val favoriteDataStoreManager: FavoriteDataStoreManager) {
    fun getRecipeById(id: Int): RecipeDto? = getRecipeByIdStub(id)
    fun getRecipesByCategoryId(categoryId: Int): List<RecipeDto> =
        getRecipesByCategoryIdStub(categoryId)

    suspend fun getRecipeUiModelById(id: Int): RecipeUiModel? {
        val dto = getRecipeById(id) ?: return null
        val isFavorite = favoriteDataStoreManager.getFavoriteIdsFlow().firstOrNull()
            ?.contains(id.toString()) == true
        return dto.toUiModel().copy(isFavorite = isFavorite)
    }
}

