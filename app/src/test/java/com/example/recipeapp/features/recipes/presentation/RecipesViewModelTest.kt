package com.example.recipeapp.features.recipes.presentation

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import com.example.recipeapp.data.model.RecipeDto
import com.example.recipeapp.data.repository.RecipesRepository
import com.example.recipeapp.navigation.Destination
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class RecipesViewModelTest {
    private val repository = mockk<RecipesRepository>()
    private val testDispatcher = UnconfinedTestDispatcher()

    private fun createViewModel(
        categoryId: Int = 1,
        categoryTitle: String = "Default Title",
        categoryImage: String = "default.jpg"
    ): RecipesViewModel {
        val savedState = SavedStateHandle().apply {
            set(Destination.CATEGORY_ID_ARG, categoryId)
            set(Destination.CATEGORY_TITLE_ARG, Uri.encode(categoryTitle))
            set(Destination.CATEGORY_IMAGE_ARG, Uri.encode(categoryImage))
        }
        return RecipesViewModel(savedState, repository)
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        clearAllMocks()
    }

    @Test
    fun `loads recipes for category`() = runTest {
        val fixtureRecipes = listOf(
            RecipeDto(
                id = 1,
                title = "Омлет",
                ingredients = emptyList(),
                method = emptyList(),
                imageUrl = null
            ),
            RecipeDto(
                id = 2,
                title = "Глазунья",
                ingredients = emptyList(),
                method = emptyList(),
                imageUrl = null
            )
        )
        every { repository.getRecipesByCategory(1) } returns flowOf(fixtureRecipes)

        val viewModel = createViewModel(categoryId = 1)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(2, state.recipes.size)
        assertEquals("Омлет", state.recipes[0].title)
        assertEquals("Default Title", state.categoryTitle)
    }

    @Test
    fun `state reflects category title from savedState`() = runTest {
        every { repository.getRecipesByCategory(42) } returns flowOf(emptyList())

        val viewModel = createViewModel(
            categoryId = 42,
            categoryTitle = "Завтраки",
            categoryImage = "breakfast.jpg"
        )

        val state = viewModel.uiState.value
        assertEquals("Завтраки", state.categoryTitle)
        assertEquals("breakfast.jpg", state.categoryImageUrl)
    }

    @Test
    fun `shows error when repository throws`() = runTest {
        every { repository.getRecipesByCategory(1) } returns flow { throw IOException("API failure") }

        val viewModel = createViewModel(categoryId = 1)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertTrue(state.error!!.contains("Не удалось загрузить рецепты"))
        assertTrue(state.recipes.isEmpty())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}