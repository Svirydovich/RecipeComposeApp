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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class RecipesRepositoryImpl @Inject constructor(
    private val apiService: RecipesApiService,
    database: RecipesDatabase
) : RecipesRepository {

    private val tag = "RecipesRepository"
    private val categoryDao = database.categoryDao()
    private val recipeDao = database.recipeDao()
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    override fun getCategories(): Flow<List<CategoryDto>> {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fresh = apiService.getCategories()
                categoryDao.upsertCategories(fresh.map { it.toEntity() })
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(tag, "Ошибка обновления: ${e.message}")
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
                Log.e(tag, "Ошибка при получении рецептов: ${e.message}")
            }
        }
        return recipeDao.getRecipesByCategory(categoryId)
            .map { entities -> entities.map { it.toDto() } }
    }

    override fun getRecipe(recipeId: Int): Flow<RecipeDto?> {
        return recipeDao.getRecipeById(recipeId)
            .map { entity -> entity?.toDto() }
            .onStart {
                repositoryScope.launch {
                    try {
                        val freshDto = apiService.getRecipe(recipeId)
                        val existingEntity = recipeDao.getRecipeById(recipeId).first()

                        if (existingEntity != null) {
                            recipeDao.upsertRecipes(listOf(freshDto.toEntity(existingEntity.categoryId)))
                        } else {
                            Log.wtf(
                                tag,
                                "Инвариант RecipePrecondition нарушен: рецепта $recipeId нет в Room перед обновлением. " +
                                        "Проверьте контракт навигации: экран деталей должен открываться только после кеширования списка."
                            )
                        }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Log.e(tag, "Сетевая ошибка обновления рецепта $recipeId", e)
                    }
                }
            }
    }

    override fun getRecipesByIds(ids: List<Int>): Flow<List<RecipeDto>> {
        if (ids.isEmpty()) return flowOf(emptyList())
        return recipeDao.getRecipesByIds(ids)
            .map { entities -> entities.map { it.toDto() } }
    }
}
