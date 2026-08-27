package com.example.recipeapp.features.details.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.recipeapp.core.util.FavoriteDataStoreManager
import com.example.recipeapp.data.repository.RecipesRepository
import com.example.recipeapp.features.details.presentation.model.RecipeDetailsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecipeDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    application: Application,
    private val repository: RecipesRepository
) : AndroidViewModel(application) {

    private val recipeId: Int = savedStateHandle.get<Int>("recipeId")
        ?: throw IllegalArgumentException("recipeId is required")
    private val favoriteManager = FavoriteDataStoreManager(application)
    private val initialState = RecipeDetailsUiState(
        recipe = null,
        portions = 1,
        isLoading = true,
        isFavorite = false
    )
    private val _uiState = MutableStateFlow(initialState)

    init {
        loadRecipe(recipeId)
    }

    fun loadRecipe(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val recipe = repository.getRecipeUiModelById(id)

            if (recipe != null) {
                _uiState.update {
                    it.copy(
                        recipe = recipe,
                        portions = recipe.servings,
                        isLoading = false,
                        error = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        recipe = null,
                        isLoading = false,
                        error = "Рецепт с ID $id не найден"
                    )
                }
            }
        }
    }

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

//    fun initializeWithRecipe(recipe: RecipeUiModel) {
//        _uiState.update { currentState ->
//            val portionsToSet = if (currentState.recipe?.id == recipe.id) {
//                currentState.portions
//            } else {
//                recipe.servings
//            }
//            currentState.copy(
//                recipe = recipe,
//                portions = portionsToSet,
//                isLoading = false,
//                error = null
//            )
//        }
//    }

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
        val baseRecipe = _uiState.value.recipe ?: return
        if (baseRecipe.servings <= 0) return
        val clampedPortions = newPortions.coerceIn(1, baseRecipe.servings * 3)
        _uiState.update { it.copy(portions = clampedPortions) }
    }
}

object RecipeDetailsViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application =
            checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
        val savedStateHandle = extras.createSavedStateHandle()

        val favoriteManager = FavoriteDataStoreManager(application)
        val repository = RecipesRepository(favoriteManager)

        return when {
            modelClass.isAssignableFrom(RecipeDetailsViewModel::class.java) -> {
                RecipeDetailsViewModel(savedStateHandle, application, repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
