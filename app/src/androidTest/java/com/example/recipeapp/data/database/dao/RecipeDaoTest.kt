package com.example.recipeapp.data.database.dao

import android.content.Context
import androidx.room3.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.recipeapp.data.database.RecipesDatabase
import com.example.recipeapp.data.database.entity.CategoryEntity
import com.example.recipeapp.data.database.entity.RecipeEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipesDaoTest {

    private lateinit var database: RecipesDatabase
    private lateinit var categoryDao: CategoryDao
    private lateinit var recipeDao: RecipeDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, RecipesDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        categoryDao = database.categoryDao()
        recipeDao = database.recipeDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertsAndRetrievesCategories() = runTest {
        val categories = listOf(
            CategoryEntity(id = 1, name = "Завтраки", description = "Лёгкие", imageUrl = ""),
            CategoryEntity(id = 2, name = "Обеды", description = "Основные", imageUrl = "")
        )

        categoryDao.upsertCategories(categories)
        val retrieved = categoryDao.getAllCategories().first()
        assertEquals(2, retrieved.size)
        assertEquals("Завтраки", retrieved.first { it.id == 1 }.name)
    }

    @Test
    fun insertReplacesDuplicateCategory() = runTest {
        val initialCategory =
            CategoryEntity(id = 1, name = "Завтраки", description = "Лёгкие", imageUrl = "")
        categoryDao.upsertCategories(listOf(initialCategory))

        val updatedCategory = CategoryEntity(
            id = 1,
            name = "Завтраки",
            description = "Сытные",
            imageUrl = "new_image.png"
        )
        categoryDao.upsertCategories(listOf(updatedCategory))

        val retrieved = categoryDao.getAllCategories().first()

        assertEquals(1, retrieved.size)
        assertEquals("Сытные", retrieved[0].description)
        assertEquals("new_image.png", retrieved[0].imageUrl)
    }

    @Test
    fun getRecipesByCategoryReturnsCorrectItems() = runTest {
        categoryDao.upsertCategories(
            listOf(
                CategoryEntity(id = 1, name = "Завтраки", description = "", imageUrl = ""),
                CategoryEntity(id = 2, name = "Обеды", description = "", imageUrl = "")
            )
        )

        val recipes = listOf(
            RecipeEntity(
                id = 1,
                title = "Омлет",
                categoryId = 1,
                imageUrl = "",
                ingredients = "",
                method = ""
            ),
            RecipeEntity(
                id = 2,
                title = "Блины",
                categoryId = 1,
                imageUrl = "",
                ingredients = "",
                method = ""
            ),
            RecipeEntity(
                id = 3,
                title = "Борщ",
                categoryId = 2,
                imageUrl = "",
                ingredients = "",
                method = ""
            )
        )

        recipeDao.upsertRecipes(recipes)

        val breakfastRecipes = recipeDao.getRecipesByCategory(1).first()

        assertEquals(2, breakfastRecipes.size)
        assertTrue(breakfastRecipes.all { it.categoryId == 1 })
        val titles = breakfastRecipes.map { it.title }
        assertTrue(titles.contains("Омлет"))
        assertTrue(titles.contains("Блины"))
        assertFalse(titles.contains("Борщ"))
    }

    @Test
    fun emptyDatabaseReturnsEmptyList() = runTest {
        val categories = categoryDao.getAllCategories().first()
        val recipes = recipeDao.getAllRecipes().first()

        assertTrue(categories.isEmpty())
        assertTrue(recipes.isEmpty())
    }
}
