package com.example.recipeapp.data.repository

import app.cash.turbine.test
import com.example.recipeapp.core.network.api.RecipesApiService
import com.example.recipeapp.data.database.RecipesDatabase
import com.example.recipeapp.data.database.dao.CategoryDao
import com.example.recipeapp.data.database.dao.RecipeDao
import com.example.recipeapp.data.database.entity.CategoryEntity
import com.example.recipeapp.data.database.entity.RecipeEntity
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class RecipesRepositoryTest {
    val recipesApiService = mockk<RecipesApiService>()
    val categoryDao = mockk<CategoryDao>()
    val recipeDao = mockk<RecipeDao>()

    private lateinit var repository: RecipesRepositoryImpl

    @Before
    fun setup() {
        every { database.categoryDao() } returns categoryDao
        every { database.recipeDao() } returns recipeDao
        repository = RecipesRepositoryImpl(recipesApiService, database)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    private val database = mockk<RecipesDatabase>(relaxed = true)

    @Test
    fun `getCategories emits categories from database`() = runTest {
        every { categoryDao.getAllCategories() } returns flowOf(
            listOf(
                CategoryEntity(
                    id = 1,
                    name = "Завтраки",
                    description = "Утренние блюда",
                    imageUrl = "breakfast.jpg"
                )
            )
        )
        coEvery { recipesApiService.getCategories() } returns emptyList()
        coEvery { categoryDao.upsertCategories(any()) } just Runs

        repository.getCategories().test {
            val categories = awaitItem()
            assertEquals(1, categories.size)
            assertEquals("Завтраки", categories[0].title)
            awaitComplete()
        }
    }

    @Test
    fun `getCategories still emits data when api throws exception`() = runTest {
        val dbData = listOf(
            CategoryEntity(
                id = 2,
                name = "Супы",
                description = null,
                imageUrl = "soups.jpg"
            )
        )

        every { categoryDao.getAllCategories() } returns flowOf(dbData)
        coEvery { recipesApiService.getCategories() } throws Exception("Network failure")
        coEvery { categoryDao.upsertCategories(any()) } just Runs

        repository.getCategories().test {
            val item = awaitItem()
            assertEquals(1, item.size)
            assertEquals("Супы", item[0].title)
            awaitComplete()
        }
    }

    @Test
    fun `getRecipesByCategory returns flow filtered by categoryId`() = runTest {
        val targetCategoryId = 42

        val entityTarget = RecipeEntity(
            id = 101,
            title = "Паста Карбонара",
            categoryId = targetCategoryId,
            imageUrl = "",
            ingredients = "[]",
            method = "[]"
        )

        every { recipeDao.getRecipesByCategory(targetCategoryId) } returns flowOf(
            listOf(
                entityTarget
            )
        )

        coEvery { recipesApiService.getRecipesByCategory(targetCategoryId) } returns emptyList()
        coEvery { recipeDao.upsertRecipes(any()) } just Runs

        repository.getRecipesByCategory(targetCategoryId).test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertNull(items[0].imageUrl)
            awaitComplete()
        }
        verify { recipeDao.getRecipesByCategory(targetCategoryId) }
    }
}
