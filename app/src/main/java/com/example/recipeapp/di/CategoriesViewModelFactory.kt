package com.example.recipeapp.di

import com.example.recipeapp.data.repository.RecipesRepository
import com.example.recipeapp.features.categories.presentation.CategoriesViewModel

class CategoriesViewModelFactory(
    private val repository: RecipesRepository
) : Factory<CategoriesViewModel> {

    override fun create(): CategoriesViewModel {
        return CategoriesViewModel(repository)
    }
}
