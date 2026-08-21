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
import com.example.recipeapp.navigation.Destination.Companion.CATEGORY_IMAGE_ARG
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class RecipesViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: RecipesRepository = RecipesRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipesUiState(isLoading = true))
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()
    val categoryImageUrl: String = Uri.decode(savedStateHandle[CATEGORY_IMAGE_ARG] ?: "")
        .let { if (it.startsWith("http")) it else "${Constants.ASSETS_URI_PREFIX}$it" }

    init {
        val rawImageUrl: String? = savedStateHandle[Destination.CATEGORY_IMAGE_ARG]

        val decodedTitle =
            Uri.decode(savedStateHandle[Destination.CATEGORY_TITLE_ARG] ?: "")
        val decodedImageUrl = Uri.decode(rawImageUrl).orEmpty()

        val categoryId: Int = requireNotNull(savedStateHandle[Destination.CATEGORY_ID_ARG]) {
            "Ошибка навигации: ${Destination.CATEGORY_ID_ARG} требуется для экрана рецептов"
        }

        loadRecipes(categoryId)

        updateHeader(decodedTitle, decodedImageUrl)
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

    private fun updateHeader(title: String, imageUrl: String) {
        _uiState.update { current ->
            current.copy(
                categoryTitle = title,
                categoryImageUrl = imageUrl
            )
        }
    }
}
