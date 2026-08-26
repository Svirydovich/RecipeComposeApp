package com.example.recipeapp.features.favorites.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.recipeapp.core.util.FavoriteDataStoreManager
import com.example.recipeapp.data.repository.RecipesRepository
import com.example.recipeapp.features.favorites.presentation.model.FavoritesUiState
import com.example.recipeapp.features.recipes.presentation.model.toUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class FavoritesViewModel(
    application: Application,
    repository: RecipesRepository,
    favoriteManager: FavoriteDataStoreManager = FavoriteDataStoreManager(
        application
    )
) :
    AndroidViewModel(application) {

    val uiState: StateFlow<FavoritesUiState> = favoriteManager
        .getFavoriteIdsFlow()
        .map { ids ->
            if (ids.isEmpty()) {
                FavoritesUiState.Empty
            } else {
                val recipes = ids.mapNotNull { idString ->
                    idString.toIntOrNull()?.let { repository.getRecipeById(it)?.toUiModel() }
                }

                if (recipes.isEmpty()) {
                    FavoritesUiState.Empty
                } else {
                    FavoritesUiState.Success(recipes)
                }
            }
        }
        .catch { error ->
            emit(FavoritesUiState.Error("Ошибка загрузки: ${error.message ?: "неизвестная ошибка"}"))
        }
        .onStart { emit(FavoritesUiState.Loading) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FavoritesUiState.Loading
        )
}


object FavoritesViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application =
            checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

        return when {
            modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> {
                FavoritesViewModel(
                    application = application,
                    repository = RecipesRepository(),
                    favoriteManager = FavoriteDataStoreManager(application)
                ) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
