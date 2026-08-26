package com.example.recipeapp.features.details.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.recipeapp.core.util.FavoriteDataStoreManager
import com.example.recipeapp.features.details.presentation.model.RecipeDetailsUiState
import com.example.recipeapp.features.recipes.presentation.model.RecipeUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecipeDetailsViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val favoriteManager = FavoriteDataStoreManager(application)
    private val initialState = RecipeDetailsUiState(
        recipe = null,
        portions = 1,
        isLoading = true,
        isFavorite = false
    )

    private val _uiState = MutableStateFlow(initialState)

    val uiState: StateFlow<RecipeDetailsUiState> =
        combine(favoriteManager.getFavoriteIdsFlow(), _uiState) { ids, state ->
            val currentId = state.recipe?.id
            if (currentId != null) {
                state.copy(isFavorite = ids.contains(currentId.toString()))
            } else {
                state
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialState
        )

    fun initializeWithRecipe(recipe: RecipeUiModel) {
        _uiState.update { currentState ->
            val portionsToSet = if (currentState.recipe?.id == recipe.id) {
                currentState.portions
            } else {
                recipe.servings
            }
            currentState.copy(
                recipe = recipe,
                portions = portionsToSet,
                isLoading = false,
                error = null
            )
        }
    }

    fun toggleFavorite() {
        val state = uiState.value
        val currentRecipeId = state.recipe?.id ?: return
        val shouldAdd = !state.isFavorite

        viewModelScope.launch {
            if (shouldAdd) favoriteManager.addFavorite(currentRecipeId)
            else favoriteManager.removeFavorite(currentRecipeId)
        }
    }

    fun updatePortions(newPortions: Int) {
        val baseServings = _uiState.value.recipe?.servings ?: 1
        val clampedPortions = newPortions.coerceIn(1, baseServings * 3)
        _uiState.update { it.copy(portions = clampedPortions) }
    }
}

object RecipeDetailsViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application =
            checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

        return when {
            modelClass.isAssignableFrom(RecipeDetailsViewModel::class.java) -> {
                RecipeDetailsViewModel(application) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
