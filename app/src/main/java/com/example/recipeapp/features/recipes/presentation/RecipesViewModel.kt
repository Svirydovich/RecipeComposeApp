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

        _uiState.update { current ->
            current.copy(
                categoryTitle = decodedTitle,
                categoryImageUrl = decodedImageUrl
            )
        }

        loadRecipes(categoryId)
    }

    private fun loadRecipes(categoryId: Int) {
        viewModelScope.launch {
            try {
                _uiState.update { current ->
                    current.copy(isLoading = true, error = null)
                }

                val recipesDto = repository.getRecipesByCategory(categoryId)

                val uiModels = recipesDto.map { it.toUiModel() }

                _uiState.update { current ->
                    current.copy(
                        recipes = uiModels,
                        isLoading = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "Неизвестная ошибка"
                    )
                }
            }
        }
    }
}
