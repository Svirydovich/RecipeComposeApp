package com.example.recipeapp.features.categories.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.data.repository.RecipesRepository
import com.example.recipeapp.features.categories.presentation.model.CategoriesUiState
import com.example.recipeapp.features.categories.presentation.model.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class CategoriesViewModel(private val repository: RecipesRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CategoriesUiState(isLoading = true))
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val categories = repository.getCategories().map { it.toUiModel() }

                _uiState.update { currentState ->
                    currentState.copy(categories = categories, isLoading = false)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "Не удалось загрузить категории"
                    )
                }
            }
        }
    }
}
