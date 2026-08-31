package com.example.recipeapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.recipeapp.core.network.api.RecipesApiService
import com.example.recipeapp.data.model.CategoryDto
import com.example.recipeapp.data.model.RecipeDto
import com.example.recipeapp.ui.theme.RecipeAppTheme
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {
    private var deepLinkIntent by mutableStateOf<Intent?>(null)
    private val threadPool: ExecutorService = Executors.newFixedThreadPool(10)
    private val okHttpClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        intent?.data?.let { _ ->
            deepLinkIntent = intent
        }

        setContent {
            RecipesApp(deepLinkIntent = deepLinkIntent)
        }



        thread {
            val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
            val retrofit = Retrofit.Builder()
                .baseUrl("https://recipes.androidsprint.ru/api/")
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
            retrofit.create(RecipesApiService::class.java)

            val request: Request = Request.Builder()
                .url("https://recipes.androidsprint.ru/api/category")
                .build()

            Log.i("MainActivity", "Выполняю запрос на потоке: ${Thread.currentThread().name}")
            try {
                okHttpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e(
                            "MainActivity",
                            "Ошибка загрузки категорий. HTTP Code: ${response.code}"
                        )
                        return@use
                    }

                    val body = response.body?.string()
                    if (body.isNullOrBlank()) {
                        Log.e("MainActivity", "Тело ответа категорий пустое.")
                        return@use
                    }
                    Log.i("MainActivity", "responseCode: ${response.code}")
                    Log.i("MainActivity", "responseMessage: ${response.message}")
                    Log.i("MainActivity", "Body: $body")

                    val categories: List<CategoryDto> = runCatching {
                        Json.decodeFromString<List<CategoryDto>>(body)
                    }.getOrElse { error ->
                        Log.e("MainActivity", "Не удалось распарсить JSON категорий", error)
                        emptyList()
                    }

                    categories.forEach { (categoryId, categoryTitle) ->

                        threadPool.execute {
                            val request: Request = Request.Builder()
                                .url("https://recipes.androidsprint.ru/api/category/$categoryId/recipes")
                                .build()

                            Log.i(
                                "MainActivity",
                                "Выполняю запрос на потоке: ${Thread.currentThread().name}"
                            )
                            try {
                                okHttpClient.newCall(request).execute().use { response ->
                                    if (!response.isSuccessful) {
                                        Log.e(
                                            "MainActivity",
                                            "Ошибка загрузки рецепта. HTTP Code: ${response.code}"
                                        )
                                        return@use
                                    }

                                    val body = response.body?.string()
                                    if (body.isNullOrBlank()) {
                                        Log.e("MainActivity", "Тело ответа рецепта пустое.")
                                        return@use
                                    }
                                    Log.i("MainActivity", "responseCode: ${response.code}")
                                    Log.i("MainActivity", "responseMessage: ${response.message}")
                                    Log.i("MainActivity", "Body: $body")

                                    val recipes: List<RecipeDto> = runCatching {
                                        Json.decodeFromString<List<RecipeDto>>(body)
                                    }.getOrElse { error ->
                                        Log.e(
                                            "MainActivity",
                                            "Не удалось распарсить JSON рецептов",
                                            error
                                        )
                                        emptyList()
                                    }

                                    Log.i(
                                        "Pool",
                                        "Категория $categoryTitle ($categoryId) на потоке ${Thread.currentThread().name}: получено рецептов ${recipes.size}"
                                    )
                                }
                            } catch (e: Exception) {
                                Log.e(
                                    "MainActivity",
                                    "Критическая ошибка в фоновом потоке загрузки данных",
                                    e
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Критическая ошибка в фоновом потоке загрузки данных", e)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        threadPool.shutdown()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.let { _ ->
            deepLinkIntent = intent
        }
        setIntent(intent)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecipesAppPreview() {
    RecipeAppTheme {
        RecipesApp()
    }
}
