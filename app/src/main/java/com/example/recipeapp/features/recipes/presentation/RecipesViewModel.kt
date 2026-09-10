package com.example.recipeapp.features.recipes.presentation

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.data.repository.RecipesRepository
import com.example.recipeapp.features.recipes.presentation.model.RecipesUiState
import com.example.recipeapp.features.recipes.presentation.model.toUiModel
import com.example.recipeapp.navigation.Destination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLDecoder
import kotlin.coroutines.cancellation.CancellationException

class RecipesViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: RecipesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipesUiState(isLoading = true))
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    init {
        val categoryId: Int = savedStateHandle[Destination.CATEGORY_ID_ARG] ?: -1
        val decodedTitle =
            Uri.decode(savedStateHandle[Destination.CATEGORY_TITLE_ARG] ?: "")
        val decodedImageUrl =
            URLDecoder.decode(savedStateHandle[Destination.CATEGORY_IMAGE_ARG] ?: "", "UTF-8")

        viewModelScope.launch {
            try {
                repository.getRecipesByCategory(categoryId)
                    .map { dtos -> dtos.map { it.toUiModel() } }
                    .collect { uiModels ->
                        _uiState.update { state ->
                            state.copy(
                                recipes = uiModels,
                                categoryTitle = decodedTitle,
                                categoryImageUrl = decodedImageUrl,
                                isLoading = state.isLoading && uiModels.isEmpty()
                            )
                        }
                    }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = "Не удалось загрузить рецепты"
                    )
                }
            }
        }
    }
}
