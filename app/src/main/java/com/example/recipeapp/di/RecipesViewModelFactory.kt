package com.example.recipeapp.di

import androidx.lifecycle.SavedStateHandle
import com.example.recipeapp.data.repository.RecipesRepository
import com.example.recipeapp.features.recipes.presentation.RecipesViewModel

class RecipesViewModelFactory(
    private val savedStateHandle: SavedStateHandle,
    private val repository: RecipesRepository
) :
    Factory<RecipesViewModel> {
    override fun create(): RecipesViewModel {
        return RecipesViewModel(savedStateHandle, repository)
    }
}
