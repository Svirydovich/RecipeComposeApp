package com.example.recipeapp.features.categories.presentation

import app.cash.turbine.test
import com.example.recipeapp.data.repository.RecipesRepository
import fixtures.CategoryTestFixtures
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
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriesViewModelTest {
    private val repository = mockk<RecipesRepository>()

    private fun createViewModel() = CategoriesViewModel(repository)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `loads categories from repository`() = runTest {
        val fixtureList = CategoryTestFixtures.createCategoryDtoList()
        every { repository.getCategories() } returns (flowOf(fixtureList))

        val viewModel = createViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNull(state.error)
            assertEquals(3, state.categories.size)
            assertEquals("Завтраки 1", state.categories[0].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `shows empty list when repository returns no data`() = runTest {
        every { repository.getCategories() } returns (flowOf(emptyList()))

        val viewModel = createViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNull(state.error)
            assertTrue(state.categories.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `shows error when repository throws`() = runTest {
        every { repository.getCategories() } returns flow { throw IOException("Network error") }

        val viewModel = createViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals("Network error", state.error)
            assertTrue(state.categories.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }
}