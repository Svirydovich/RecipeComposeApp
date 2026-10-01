package com.example.recipeapp.features.recipes.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.example.recipeapp.features.recipes.presentation.model.IngredientUiModel
import com.example.recipeapp.features.recipes.presentation.model.RecipeUiModel
import com.example.recipeapp.features.recipes.presentation.model.RecipesUiState
import org.junit.Rule
import org.junit.Test

class RecipesContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsLoadingState() {
        composeTestRule.setContent {
            RecipesContent(
                uiState = RecipesUiState(isLoading = true),
                onRecipeClick = {}
            )
        }
        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }

    @Test
    fun showsErrorState() {
        composeTestRule.setContent {
            RecipesContent(
                uiState = RecipesUiState(error = "Network error"),
                onRecipeClick = {}
            )
        }
        composeTestRule.onNodeWithTag("error_message").assertIsDisplayed()
    }

    @Test
    fun showsEmptyState() {
        composeTestRule.setContent {
            RecipesContent(
                uiState = RecipesUiState(),
                onRecipeClick = {}
            )
        }
        composeTestRule.onNodeWithTag("empty_state").assertIsDisplayed()
    }

    @Test
    fun displaysRecipeList() {
        val recipe = RecipeUiModel(
            id = 1,
            title = "Омлет",
            imageUrl = "omelette.jpg",
            ingredients = listOf(
                IngredientUiModel(
                    name = "Яйца",
                    quantity = "2",
                    unitOfMeasure = "шт."
                ),
                IngredientUiModel(
                    name = "Молоко",
                    quantity = "100",
                    unitOfMeasure = "мл"
                )
            ),
            method = listOf(
                "1. Взбить яйца.",
                "2. Добавить молоко.",
                "3. Жарить 5 минут."
            ),
            isFavorite = false
        )

        composeTestRule.setContent {
            RecipesContent(
                uiState = RecipesUiState(
                    recipes = listOf(recipe),
                    categoryTitle = "Завтраки"
                ),
                onRecipeClick = {}
            )
        }

        composeTestRule.onNodeWithText("ЗАВТРАКИ").assertIsDisplayed()
        composeTestRule.onNodeWithText("ОМЛЕТ").assertIsDisplayed()
        composeTestRule.onNodeWithText("Яйца").assertIsDisplayed()
    }
}