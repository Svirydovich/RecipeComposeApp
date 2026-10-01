package com.example.recipeapp.features.categories.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.recipeapp.features.categories.presentation.model.CategoriesUiState
import com.example.recipeapp.features.categories.presentation.model.CategoryUiModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoriesContentTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysCategories() {
        composeTestRule.setContent {
            CategoriesContent(
                uiState = CategoriesUiState(
                    categories = listOf(
                        CategoryUiModel(
                            id = 1,
                            title = "Завтраки",
                            description = "Утренние блюда",
                            imageUrl = "breakfast.jpg"
                        )
                    )
                ),
                onCategoryClick = { _, _, _ -> }
            )
        }
        composeTestRule.onNodeWithText("ЗАВТРАКИ").assertIsDisplayed()
    }

    @Test
    fun clickingCategoryNavigatesToRecipes() {
        var clickedId: Int? = null
        composeTestRule.setContent {
            CategoriesContent(
                uiState = CategoriesUiState(
                    categories = listOf(
                        CategoryUiModel(
                            id = 1,
                            title = "Завтраки",
                            description = "Утренние блюда",
                            imageUrl = "breakfast.jpg"
                        )
                    )
                ),
                onCategoryClick = { id, _, _ -> clickedId = id }
            )
        }
        composeTestRule.onNodeWithText("ЗАВТРАКИ").performClick()
        assertEquals(1, clickedId)
    }

    @Test
    fun showsLoadingState() {
        composeTestRule.setContent {
            CategoriesContent(
                uiState = CategoriesUiState(isLoading = true),
                onCategoryClick = { _, _, _ -> })
        }
        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }
}
