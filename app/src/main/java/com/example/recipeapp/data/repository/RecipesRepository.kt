package com.example.recipeapp.data.repository

import com.example.recipeapp.data.model.CategoryDto
import com.example.recipeapp.data.model.RecipeDto
import kotlinx.coroutines.flow.Flow

interface RecipesRepository {
    fun getCategories(): Flow<List<CategoryDto>>

    fun getRecipesByCategory(categoryId: Int): Flow<List<RecipeDto>>

    suspend fun getRecipe(recipeId: Int): RecipeDto

    fun getRecipesByIds(ids: List<Int>): Flow<List<RecipeDto>>
}
