package com.example.recipeapp.di

import android.app.Application
import com.example.recipeapp.features.favorites.presentation.FavoritesViewModel

class FavoritesViewModelFactory(
    private val application: Application
) : Factory<FavoritesViewModel> {
    override fun create(): FavoritesViewModel {
        val container = (application as RecipeApplication).appContainer
        return FavoritesViewModel(application, container.recipesRepository)
    }
}
