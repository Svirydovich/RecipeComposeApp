package com.example.recipeapp.di

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.example.recipeapp.data.repository.RecipesRepository
import com.example.recipeapp.features.details.presentation.RecipeDetailsViewModel

class RecipeDetailsViewModelFactory(
    private val application: Application,
    private val savedStateHandle: SavedStateHandle,
    private val repository: RecipesRepository
) : Factory<RecipeDetailsViewModel> {
    override fun create(): RecipeDetailsViewModel {
        return RecipeDetailsViewModel(application, savedStateHandle, repository)
    }
}
