package com.example.recipeapp

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.recipeapp.core.ui.navigation.BottomNavigation
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
import kotlinx.coroutines.delay

@Composable
fun RecipesApp(deepLinkIntent: Intent? = null) {
    val mainViewModel: MainViewModel = hiltViewModel()
    val favoriteCount by mainViewModel.favoriteCount.collectAsState()

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
                        }
                    },
                    onFavoriteClick = {
                        navController.navigate(Destination.Favorites.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
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
                    val categoriesViewModel: CategoriesViewModel = hiltViewModel()
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
                    val favoritesViewModel: FavoritesViewModel = hiltViewModel()
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
                    val recipesViewModel: RecipesViewModel = hiltViewModel()
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
                    val viewModel: RecipeDetailsViewModel = hiltViewModel()
                    RecipeDetailsRoute(viewModel = viewModel)
                }
            }
        }
    }
}
