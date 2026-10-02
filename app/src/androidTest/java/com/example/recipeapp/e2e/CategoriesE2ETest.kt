package com.example.recipeapp.e2e

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.recipeapp.MainActivity
import com.example.recipeapp.screen.CategoriesComposeScreen
import com.example.recipeapp.screen.RecipesComposeScreen
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoriesE2ETest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun categoriesScreenLoadsContent() = run {
        step("Открыть приложение и проверить экран категорий") {
            ComposeScreen.onComposeScreen<CategoriesComposeScreen>(composeTestRule) {
                categoriesGrid { assertIsDisplayed() }
            }
        }
    }

    @Test
    fun clickingCategoryOpensRecipesScreen() = run {
        step("Дождаться загрузки категорий") {
            ComposeScreen.onComposeScreen<CategoriesComposeScreen>(composeTestRule) {
                categoriesGrid { assertIsDisplayed() }
            }
        }
        step("Нажать на первую категорию") {
            ComposeScreen.onComposeScreen<CategoriesComposeScreen>(composeTestRule) {
                categoryItem { performClick() }
            }
        }
        step("Проверить что открылся экран рецептов") {
            ComposeScreen.onComposeScreen<RecipesComposeScreen>(composeTestRule) {
                assertIsDisplayed()
                loadingIndicator { assertIsDisplayed() }
            }
        }
    }
}
