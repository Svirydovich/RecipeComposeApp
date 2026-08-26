package com.example.recipeapp.features.details.presentation.model


import com.example.recipeapp.features.recipes.presentation.model.IngredientUiModel
import com.example.recipeapp.features.recipes.presentation.model.RecipeUiModel
import com.example.recipeapp.features.recipes.presentation.model.formatIngredientAmount

data class RecipeDetailsUiState(
    val recipe: RecipeUiModel? = null,
    val portions: Int,
    val isLoading: Boolean,
    val error: String? = null,
    val isFavorite: Boolean
) {
    val scaledIngredients: List<IngredientUiModel>
        get() {
            val baseRecipe = recipe ?: return emptyList()
            if (baseRecipe.servings <= 0) return emptyList()
            val multiplier = portions.coerceAtLeast(1).toFloat() / baseRecipe.servings.toFloat()
            return baseRecipe.ingredients.map { ingredient ->
                val originalQuantity = ingredient.quantity.toFloatOrNull()
                ingredient.copy(
                    quantity = if (originalQuantity != null) {
                        (originalQuantity * multiplier).formatIngredientAmount()
                    } else {
                        ingredient.quantity
                    }
                )
            }
        }
}
