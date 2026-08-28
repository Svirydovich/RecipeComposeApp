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
import com.example.recipeapp.ui.theme.RecipeAppTheme
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    private var deepLinkIntent by mutableStateOf<Intent?>(null)

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

        val thread = Thread {
            Log.i("MainActivity", "Выполняю запрос на потоке: ${Thread.currentThread().name}")

            try {
                val url = URL("https://recipes.androidsprint.ru/api/category")
                val connection = url.openConnection() as HttpURLConnection

                try {
                    connection.connect()

                    val code = connection.responseCode

                    if (code in 200..299) {
                        val jsonString = connection.inputStream.bufferedReader().readText()

                        Log.i("MainActivity", "responseCode: ${connection.responseCode}")
                        Log.i("MainActivity", "responseMessage: ${connection.responseMessage}")
                        Log.i("MainActivity", "Body: $jsonString")

                        val categories = Json.decodeFromString<List<CategoryDto>>(jsonString)
                        Log.i(
                            "MainActivity",
                            "Получено категорий: ${categories.size}: ${categories.joinToString(", ") { it.title }}"
                        )
                    } else {
                        connection.errorStream?.bufferedReader()?.readText() ?: ""
                        Log.i(
                            "MainActivity",
                            "Ошибка HTTP. Код: $code. Сообщение: ${connection.responseMessage}"
                        )
                    }
                } finally {
                    connection.disconnect()
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Ошибка при выполнении запроса", e)
            }
        }

        thread.start()
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
