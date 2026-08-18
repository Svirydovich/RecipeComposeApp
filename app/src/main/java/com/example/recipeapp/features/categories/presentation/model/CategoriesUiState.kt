package com.example.recipeapp.features.categories.presentation.model

data class CategoriesUiState(
    val categories: List<CategoryUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
