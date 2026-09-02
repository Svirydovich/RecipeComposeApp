package com.example.recipeapp

import android.app.Application
import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.recipeapp.core.network.NetworkConfig
import com.example.recipeapp.core.network.api.RecipesApiService
import com.example.recipeapp.core.ui.navigation.BottomNavigation
import com.example.recipeapp.data.repository.RecipesRepositoryImpl
import com.example.recipeapp.features.categories.presentation.CategoriesViewModel
import com.example.recipeapp.features.categories.ui.CategoriesScreen
import com.example.recipeapp.features.details.presentation.RecipeDetailsViewModel
import com.example.recipeapp.features.details.ui.RecipeDetailsRoute
import com.example.recipeapp.features.favorites.presentation.FavoritesViewModel
import com.example.recipeapp.features.favorites.ui.FavoritesRoute
import com.example.recipeapp.features.recipes.presentation.RecipesViewModel
import com.example.recipeapp.features.recipes.ui.RecipesScreen
import com.example.recipeapp.navigation.Destination
import com.example.recipeapp.navigation.Destination.Companion.DEEP_LINK_SCHEME
import com.example.recipeapp.ui.theme.RecipeAppTheme
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

@Composable
fun RecipesApp(deepLinkIntent: Intent? = null) {
    val mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory)
    val favoriteCount by mainViewModel.favoriteCount.collectAsState()

    val json = remember {
        Json { ignoreUnknownKeys = true; coerceInputValues = true }
    }
    val apiService = remember {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(RecipesApiService::class.java)
    }
    val repository = remember { RecipesRepositoryImpl(apiService) }

    RecipeAppTheme {
        val navController = rememberNavController()

        LaunchedEffect(deepLinkIntent) {
            deepLinkIntent?.data?.let { uri ->
                val recipeId: Int? = when (uri.scheme) {
                    DEEP_LINK_SCHEME ->
                        if (uri.host == "recipe") uri.pathSegments.getOrNull(0)?.toIntOrNull()
                        else null

                    "https", "http" ->
                        if (uri.pathSegments.getOrNull(0) == "recipe") uri.pathSegments.getOrNull(1)
                            ?.toIntOrNull()
                        else null

                    else -> null
                }

                if (recipeId != null) {
                    delay(100)
                    navController.navigate(Destination.Details.createRoute(recipeId))
                }
            }
        }

        Scaffold(
            bottomBar = {
                BottomNavigation(
                    onCategoriesClick = {
                        navController.navigate(Destination.Categories.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onFavoriteClick = {
                        navController.navigate(Destination.Favorites.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    favoriteCount = favoriteCount
                )
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Destination.Categories.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Destination.Categories.route) {
                    val categoriesViewModel = remember { CategoriesViewModel(repository) }
                    val uiState by categoriesViewModel.uiState.collectAsState()
                    CategoriesScreen(
                        uiState = uiState,
                        modifier = Modifier,
                        onCategoryClick = { categoryId, categoryTitle, imageURL ->
                            navController.navigate(
                                Destination.Recipes.createRoute(
                                    categoryId,
                                    categoryTitle,
                                    imageURL
                                )
                            )
                        }
                    )
                }

                composable(Destination.Favorites.route) {
                    val context = LocalContext.current
                    val application =
                        context.applicationContext as? Application ?: return@composable
                    val favoritesViewModel = remember {
                        FavoritesViewModel(application, repository)
                    }
                    FavoritesRoute(viewModel = favoritesViewModel, onRecipeClick = { recipeId ->
                        navController.navigate(Destination.Details.createRoute(recipeId))
                    })
                }

                composable(
                    route = Destination.Recipes.route,
                    arguments = listOf(
                        navArgument(Destination.CATEGORY_ID_ARG) { type = NavType.IntType },
                        navArgument(Destination.CATEGORY_TITLE_ARG) {
                            type = NavType.StringType
                            defaultValue = ""
                        },
                        navArgument(Destination.CATEGORY_IMAGE_ARG) {
                            type = NavType.StringType
                            defaultValue = ""
                        }
                    )
                ) {
                    val backStackEntry = navController.currentBackStackEntry
                    val savedStateHandle = backStackEntry?.savedStateHandle ?: return@composable
                    val recipesViewModel: RecipesViewModel =
                        remember(backStackEntry) { RecipesViewModel(savedStateHandle, repository) }
                    val uiState by recipesViewModel.uiState.collectAsState()
                    RecipesScreen(
                        modifier = Modifier,
                        onRecipeClick = { recipe ->
                            navController.navigate(Destination.Details.createRoute(recipe.id))
                        },
                        uiState = uiState
                    )
                }

                composable(
                    route = Destination.Details.route,
                    arguments = listOf(
                        navArgument(Destination.RECIPE_ID_ARG) { type = NavType.IntType }
                    )
                ) {
                    val backStackEntry = navController.currentBackStackEntry
                    val savedStateHandle = backStackEntry?.savedStateHandle ?: return@composable
                    val context = LocalContext.current
                    val application =
                        context.applicationContext as? Application ?: return@composable

                    val viewModel: RecipeDetailsViewModel = remember(backStackEntry) {
                        RecipeDetailsViewModel(application, savedStateHandle, repository)
                    }
                    RecipeDetailsRoute(viewModel = viewModel)
                }
            }
        }
    }
}
