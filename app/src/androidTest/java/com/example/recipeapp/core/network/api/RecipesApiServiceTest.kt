package com.example.recipeapp.core.network.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.recipeapp.core.network.NetworkConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.HttpException
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class RecipesApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: RecipesApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val appJson = NetworkConfig.json
        val jsonConverter = appJson.asConverterFactory("application/json".toMediaType())

        val api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(jsonConverter)
            .build()

        apiService = api.create(RecipesApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getCategoriesDeserializesJsonCorrectly() = runTest {
        val jsonResponse = """
            [
                {
                    "id": 1,
                    "title": "Завтраки",
                    "description": "Лёгкие и быстрые",
                    "imageUrl": "breakfast.jpg"
                },
                {
                    "id": 2,
                    "title": "Обеды",
                    "description": "Основные блюда",
                    "imageUrl": "lunch.jpg"
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json")
        )

        val categories = apiService.getCategories()

        val request = mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        assertNotNull(request)
        assertEquals("/category", request!!.path)
        assertEquals("GET", request.method)
        assertEquals(2, categories.size)

        val first = categories[0]
        assertEquals(1, first.id)
        assertEquals("Завтраки", first.title)
        assertEquals("Лёгкие и быстрые", first.description)
        assertEquals("breakfast.jpg", first.imageUrl)

        val second = categories[1]
        assertEquals(2, second.id)
        assertEquals("Обеды", second.title)
    }

    @Test
    fun getCategoriesHandlesEmptyList() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("[]")
                .addHeader("Content-Type", "application/json")
        )

        val categories = apiService.getCategories()
        assertTrue(categories.isEmpty())
    }

    @Test
    fun getCategoriesThrowsOnServerError() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("""{"message": "Internal Server Error"}""")
        )

        try {
            apiService.getCategories()
            fail("Expected HttpException")
        } catch (e: HttpException) {
            assertEquals(500, e.code())
        }
    }
}
