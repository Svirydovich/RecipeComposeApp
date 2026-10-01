package com.example.recipeapp.data.repository

import android.content.Context
import androidx.room3.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.example.recipeapp.core.network.NetworkConfig
import com.example.recipeapp.core.network.api.RecipesApiService
import com.example.recipeapp.data.database.RecipesDatabase
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit

@RunWith(AndroidJUnit4::class)
class CompleteDataFlowTest {

    private lateinit var database: RecipesDatabase
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, RecipesDatabase::class.java).build()
        mockWebServer = MockWebServer().also { it.start() }
    }

    @After
    fun tearDown() {
        database.close()
        mockWebServer.shutdown()
    }

    @Test
    fun categoriesAreLoadedFromApiAndStoredInCache() = runTest {
        mockWebServer.enqueue(
            MockResponse().setBody(
                """[{"id":1,"title":"Завтраки","description":"Лёгкие блюда","imageUrl":"breakfast.jpg"}]"""
            ).setResponseCode(200)
        )

        val apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(
                NetworkConfig.json.asConverterFactory("application/json".toMediaType())
            )
            .build()
            .create(RecipesApiService::class.java)

        val repository = RecipesRepositoryImpl(apiService = apiService, database = database)

        repository.getCategories().test {
            assertTrue(awaitItem().isEmpty())
            val loaded = awaitItem()
            assertEquals("Завтраки", loaded.first().title)
            cancelAndIgnoreRemainingEvents()
        }

        val cached = database.categoryDao().getAllCategories().first()
        assertEquals(1, cached.size)
        assertEquals("Завтраки", cached.first().name)
    }
}
