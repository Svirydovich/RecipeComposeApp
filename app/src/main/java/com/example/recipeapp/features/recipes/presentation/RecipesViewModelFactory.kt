package com.example.recipeapp.features.recipes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.recipeapp.core.util.FavoriteDataStoreManager
import com.example.recipeapp.data.repository.RecipesRepository

class RecipesViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application =
            checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
        val savedStateHandle = extras.createSavedStateHandle()

        val favoriteManager = FavoriteDataStoreManager(application)
        val repository = RecipesRepository(favoriteManager)

        return when {
            modelClass.isAssignableFrom(RecipesViewModel::class.java) -> {
                modelClass.cast(RecipesViewModel(savedStateHandle, repository))
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
