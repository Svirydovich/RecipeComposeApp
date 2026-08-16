package com.example.recipeapp.data.repository

import com.example.recipeapp.data.model.RecipeDto

class RecipesRepository {
    fun getRecipeById(id: Int): RecipeDto? = getRecipeByIdStub(id)
}
