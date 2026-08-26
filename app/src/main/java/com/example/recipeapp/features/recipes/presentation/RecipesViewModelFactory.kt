package com.example.recipeapp.features.recipes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras

class RecipesViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val savedStateHandle = extras.createSavedStateHandle()
        return when {
            modelClass.isAssignableFrom(RecipesViewModel::class.java) ->
                RecipesViewModel(savedStateHandle) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
