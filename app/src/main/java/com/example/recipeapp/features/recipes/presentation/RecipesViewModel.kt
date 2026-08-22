package com.example.recipeapp.features.recipes.presentation

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.Constants
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
    private val repository: RecipesRepository = RecipesRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipesUiState(isLoading = true))
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    init {
        val categoryId: Int = requireNotNull(savedStateHandle[Destination.CATEGORY_ID_ARG]) {
            "Ошибка навигации: ${Destination.CATEGORY_ID_ARG} требуется для экрана рецептов"
        }
        val decodedTitle =
            Uri.decode(savedStateHandle[Destination.CATEGORY_TITLE_ARG] ?: "")
        val decodedImageUrl =
            URLDecoder.decode(savedStateHandle[Destination.CATEGORY_IMAGE_ARG] ?: "", "UTF-8")

        _uiState.update { current ->
            current.copy(
                categoryTitle = decodedTitle,
                categoryImageUrl = decodedImageUrl.let {
                    when {
                        it.isEmpty() -> it
                        it.startsWith("http") -> it
                        else -> "${Constants.ASSETS_URI_PREFIX}$it"
                    }
                }
            )
        }

        loadRecipes(categoryId)
    }

    private fun loadRecipes(categoryId: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val recipesDto = repository.getRecipesByCategoryId(categoryId)

                val uiModels = recipesDto.map { it.toUiModel() }

                _uiState.value = _uiState.value.copy(
                    recipes = uiModels,
                    isLoading = false
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Неизвестная ошибка"
                )
            }
        }
    }
}
