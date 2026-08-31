package com.example.recipeapp.data.repository

import com.example.recipeapp.core.network.api.RecipesApiService
import com.example.recipeapp.data.model.CategoryDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipesRepositoryImpl(
    private val apiService: RecipesApiService
) : RecipesRepository {

    override suspend fun getCategories(): List<CategoryDto> {
        return withContext(Dispatchers.IO) {
            apiService.getCategories()
        }
    }
}
