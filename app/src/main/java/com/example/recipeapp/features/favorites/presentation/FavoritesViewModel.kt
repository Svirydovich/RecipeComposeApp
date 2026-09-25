package com.example.recipeapp.features.favorites.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.core.util.FavoriteDataStoreManager
import com.example.recipeapp.data.repository.RecipesRepository
import com.example.recipeapp.features.favorites.presentation.model.FavoritesUiState
import com.example.recipeapp.features.recipes.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    repository: RecipesRepository,
    favoriteManager: FavoriteDataStoreManager
) :
    ViewModel() {

    val uiState: StateFlow<FavoritesUiState> = favoriteManager
        .getFavoriteIdsFlow()
        .flatMapLatest { ids ->
            repository.getRecipesByIds(ids.mapNotNull { it.toIntOrNull() })
        }
        .map { recipes ->
            if (recipes.isEmpty()) FavoritesUiState.Empty
            else FavoritesUiState.Success(recipes.map { it.toUiModel() })
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
