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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import com.example.recipeapp.data.model.CategoryDto
import com.example.recipeapp.data.model.RecipeDto
import com.example.recipeapp.ui.theme.RecipeAppTheme
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.serialization.json.Json
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    private var deepLinkIntent by mutableStateOf<Intent?>(null)
    private val threadPool: ExecutorService = Executors.newFixedThreadPool(10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        intent?.data?.let { _ ->
            deepLinkIntent = intent
        }

        setContent {
            RecipesApp(deepLinkIntent = deepLinkIntent)
        }

        Log.i(
            "MainActivity",
            "Метод onCreate() выполняется на потоке: ${Thread.currentThread().name}"
        )

        threadPool.execute {
            Log.i("MainActivity", "Выполняю запрос на потоке: ${Thread.currentThread().name}")

            try {
                val url = URL("https://recipes.androidsprint.ru/api/category")
                val connection = url.openConnection() as HttpURLConnection

                try {
                    connection.connect()

                    val jsonString = connection.inputStream.bufferedReader().use { it.readText() }

                    Log.i("MainActivity", "responseCode: ${connection.responseCode}")
                    Log.i("MainActivity", "responseMessage: ${connection.responseMessage}")
                    Log.i("MainActivity", "Body: $jsonString")

                    val categories = Json.decodeFromString<List<CategoryDto>>(jsonString)
                    Log.i("Pool", "Получено категорий: ${categories.size}")

                    val categoryTitleById: Map<Int, String> =
                        categories.associate { it.id to it.title }

                    categoryTitleById.forEach { (categoryId, categoryTitle) ->
                        threadPool.execute {
                            Log.i(
                                "Pool",
                                "Запрос рецептов категории $categoryId на потоке: ${Thread.currentThread().name}"
                            )

                            try {
                                val recipesUrl =
                                    URL("https://recipes.androidsprint.ru/api/category/$categoryId/recipes")
                                val recipesConnection =
                                    recipesUrl.openConnection() as? HttpURLConnection
                                if (recipesConnection != null) {
                                    try {
                                        recipesConnection.connect()

                                        val recipesJson =
                                            recipesConnection.inputStream.bufferedReader()
                                                .use { it.readText() }
                                        val recipes: List<RecipeDto> =
                                            Json.decodeFromString(recipesJson)

                                        Log.i(
                                            "Pool",
                                            "Категория $categoryTitle ($categoryId): получено рецептов ${recipes.size}"
                                        )
                                    } finally {
                                        recipesConnection.disconnect()
                                    }
                                } else {
                                    Log.e(
                                        "Pool",
                                        "Неожиданный тип соединения для URL: $recipesUrl. Требуется HttpURLConnection"
                                    )
                                }
                            } catch (e: Exception) {
                                Log.e(
                                    "Pool",
                                    "Ошибка при получении рецептов категории $categoryId",
                                    e
                                )
                            }
                        }
                    }
                } finally {
                    connection.disconnect()
                }
            } catch (e: Exception) {
                Log.e(
                    "Pool",
                    "Ошибка при выполнении запроса",
                    e
                )
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
