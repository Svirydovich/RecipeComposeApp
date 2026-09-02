package com.example.recipeapp.data.repository

import android.util.Log
import com.example.recipeapp.core.network.api.RecipesApiService
import com.example.recipeapp.data.model.CategoryDto
import com.example.recipeapp.data.model.RecipeDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class RecipesRepositoryImpl(
    private val apiService: RecipesApiService
) : RecipesRepository {

    override suspend fun getCategories(): List<CategoryDto> {
        return withContext(Dispatchers.IO) {
            try {
                apiService.getCategories()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("RecipesRepository", "Ошибка при получении категорий", e)
                emptyList()
            }
        }
    }

    override suspend fun getRecipesByCategory(categoryId: Int): List<RecipeDto> {
        return withContext(Dispatchers.IO) {
            try {
                apiService.getRecipesByCategory(categoryId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("RecipesRepository", "Ошибка при получении рецептов", e)
                emptyList()
            }
        }
    }

    override suspend fun getRecipe(recipeId: Int): RecipeDto {
        return withContext(Dispatchers.IO) {
            try {
                apiService.getRecipe(recipeId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("RecipesRepository", "Ошибка при получении рецепта", e)
                throw Exception("Ошибка при получении рецепта", e)
            }
        }
    }
}
