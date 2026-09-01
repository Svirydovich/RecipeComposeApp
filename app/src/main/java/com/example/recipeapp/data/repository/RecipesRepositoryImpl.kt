package com.example.recipeapp.data.repository

import com.example.recipeapp.core.network.api.RecipesApiService
import com.example.recipeapp.data.model.CategoryDto
import com.example.recipeapp.data.model.RecipeDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class RecipesRepositoryImpl(
    private val apiService: RecipesApiService
) : RecipesRepository {

    override suspend fun getCategories(): List<CategoryDto> {
        return withContext(Dispatchers.IO) {
            try {
                apiService.getCategories()
            } catch (e: HttpException) {
                throw Exception("Ошибка сервера при получении категорий (код ${e.code()})", e)
            } catch (e: IOException) {
                throw Exception("Ошибка сети при получении категорий", e)
            } catch (e: Exception) {
                throw Exception("Неизвестная ошибка при получении категорий", e)

            }
        }
    }

    override suspend fun getRecipesByCategory(categoryId: Int): List<RecipeDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRecipesByCategory(categoryId).execute()
                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun getRecipe(recipeId: Int): RecipeDto? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRecipe(recipeId).execute()

                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}

