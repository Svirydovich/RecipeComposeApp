package com.example.recipeapp.data.repository

import android.content.Context
import androidx.room3.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.example.recipeapp.core.network.api.RecipesApiService
import com.example.recipeapp.data.database.RecipesDatabase
import com.example.recipeapp.data.database.dao.CategoryDao
import com.example.recipeapp.data.model.CategoryDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RecipesRepositoryIntegrationTest {

    private lateinit var database: RecipesDatabase
    private lateinit var categoryDao: CategoryDao
    private val apiService = mockk<RecipesApiService>()

    private lateinit var repository: RecipesRepositoryImpl

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, RecipesDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        categoryDao = database.categoryDao()
        repository = RecipesRepositoryImpl(apiService = apiService, database = database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun savesDataToCacheAfterSuccessfulApiCall() = runTest {
        coEvery { apiService.getCategories() } returns listOf(
            CategoryDto(
                id = 1,
                title = "Завтраки",
                description = "Лёгкие",
                imageUrl = "breakfast.jpg"
            )
        )

        repository.getCategories().test {
            awaitItem()
            val loaded = awaitItem()
            assertEquals("Завтраки", loaded.first().title)
            cancelAndIgnoreRemainingEvents()
        }

        val cached = categoryDao.getAllCategories().first()
        assertEquals(1, cached.size)
        assertEquals("Завтраки", cached[0].name)
    }

    @Test
    fun returnsCachedDataWhenApiFails() = runTest {
        val cachedEntity = com.example.recipeapp.data.database.entity.CategoryEntity(
            id = 1,
            name = "Обеды из кеша",
            description = "Сытные",
            imageUrl = "lunch.jpg"
        )
        categoryDao.upsertCategories(listOf(cachedEntity))

        coEvery { apiService.getCategories() } throws IOException("Network error")

        repository.getCategories().test {
            val cached = awaitItem()
            assertEquals(1, cached.size)
            assertEquals("Обеды из кеша", cached.first().title)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
