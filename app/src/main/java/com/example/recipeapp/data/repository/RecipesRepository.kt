package com.example.recipeapp.data.repository

import com.example.recipeapp.data.model.RecipeDto

class RecipesRepository {
    fun getRecipeById(id: Int): RecipeDto? = getRecipeByIdStub(id)
    fun getRecipesByCategoryId(categoryId: Int): List<RecipeDto> = getRecipesByCategoryIdStub(categoryId)
}
