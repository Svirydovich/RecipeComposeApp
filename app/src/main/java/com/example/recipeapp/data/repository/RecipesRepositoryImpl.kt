package com.example.recipeapp.data.repository

import android.util.Log
import com.example.recipeapp.core.network.api.RecipesApiService
import com.example.recipeapp.data.database.RecipesDatabase
import com.example.recipeapp.data.model.CategoryDto
import com.example.recipeapp.data.model.RecipeDto
import com.example.recipeapp.data.model.toDto
import com.example.recipeapp.data.model.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class RecipesRepositoryImpl(
    private val apiService: RecipesApiService,
    database: RecipesDatabase
) : RecipesRepository {

    private val categoryDao = database.categoryDao()
    private val recipeDao = database.recipeDao()


    override fun getCategories(): Flow<List<CategoryDto>> {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fresh = apiService.getCategories()
                categoryDao.upsertCategories(fresh.map { it.toEntity() })
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("RecipesRepository", "Ошибка обновления: ${e.message}")
            }
        }
        return categoryDao.getAllCategories().map { entities -> entities.map { it.toDto() } }
    }

    override fun getRecipesByCategory(categoryId: Int): Flow<List<RecipeDto>> {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fresh = apiService.getRecipesByCategory(categoryId)
                recipeDao.upsertRecipes(fresh.map { it.toEntity(categoryId) })
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("RecipesRepository", "Ошибка при получении рецептов: ${e.message}")
            }
        }
        return recipeDao.getRecipesByCategory(categoryId)
            .map { entities -> entities.map { it.toDto() } }
    }

    override suspend fun getRecipe(recipeId: Int): RecipeDto {
        return withContext(Dispatchers.IO) {
            apiService.getRecipe(recipeId)
        }
    }

    override fun getRecipesByIds(ids: List<Int>): Flow<List<RecipeDto>> {
        if (ids.isEmpty()) return flowOf(emptyList())
        return recipeDao.getRecipesByIds(ids)
            .map { entities -> entities.map { it.toDto() } }
    }
}
