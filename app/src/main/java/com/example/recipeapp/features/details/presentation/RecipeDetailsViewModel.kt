package com.example.recipeapp.features.details.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.core.util.FavoriteDataStoreManager
import com.example.recipeapp.data.repository.RecipesRepository
import com.example.recipeapp.features.details.presentation.model.RecipeDetailsUiState
import com.example.recipeapp.features.recipes.presentation.model.RecipeUiModel
import com.example.recipeapp.features.recipes.presentation.model.toUiModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

@OptIn(FlowPreview::class)
class RecipeDetailsViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
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
        _uiState.update { it.copy(isLoading = true, error = null) }
        var hasReceivedValidData = false

        val recipeFlow = flow { emit(Unit) }
            .flatMapLatest {
                repository.getRecipe(recipeId)
                    .map { dto ->
                        hasReceivedValidData = true
                        updateStateWithRecipe(dto?.toUiModel())
                    }
            }
            .onStart {
            }
            .catch { e ->
                _uiState.update {
                    it.copy(isLoading = false, error = "Критическая ошибка хранилища")
                }
            }

        viewModelScope.launch {
            launch {
                delay(8000)
                if (_uiState.value.recipe == null &&
                    _uiState.value.error.isNullOrEmpty()
                ) {

                    _uiState.update {
                        it.copy(isLoading = false, error = "Проверьте подключение к интернету")
                    }
                }
            }

            try {
                recipeFlow.collect { }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (_uiState.value.error.isNullOrEmpty()) {
                    _uiState.update { it.copy(isLoading = false, error = "Неизвестная ошибка") }
                }
            }
        }
    }

    private fun updateStateWithRecipe(uiRecipe: RecipeUiModel?) {
        if (uiRecipe != null) {
            _uiState.update { state ->
                state.copy(
                    recipe = uiRecipe,
                    portions = if (state.portions == 1 && state.recipe == null) uiRecipe.servings else state.portions,
                    isLoading = false,
                    error = null
                )
            }
        } else {
            _uiState.update { it.copy(isLoading = true, error = null) }
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
