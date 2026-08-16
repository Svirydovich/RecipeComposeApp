package com.example.recipeapp

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.recipeapp.core.ui.navigation.BottomNavigation
import com.example.recipeapp.core.util.FavoriteDataStoreManager
import com.example.recipeapp.data.repository.getRecipeByIdStub
import com.example.recipeapp.navigation.Destination
import com.example.recipeapp.navigation.Destination.Companion.DEEP_LINK_SCHEME
import com.example.recipeapp.ui.categories.CategoriesScreen
import com.example.recipeapp.ui.details.RecipeDetailsRoute
import com.example.recipeapp.ui.favorites.FavoritesScreen
import com.example.recipeapp.ui.recipes.RecipesScreen
import com.example.recipeapp.data.repository.RecipesRepository
import com.example.recipeapp.ui.recipes.model.toUiModel
import com.example.recipeapp.ui.theme.RecipeAppTheme
import kotlinx.coroutines.delay

@Composable
fun RecipesApp(deepLinkIntent: Intent? = null) {
    val context = LocalContext.current
    val favoritesManager = remember { FavoriteDataStoreManager(context) }

    RecipeAppTheme {
        val navController = rememberNavController()
        val favoriteCount by remember(favoritesManager) {
            favoritesManager.getFavoriteCountFlow()
        }.collectAsState(initial = 0)

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
                    CategoriesScreen(
                        modifier = Modifier,
                        onCategoryClick = { categoryId, categoryTitle ->
                            navController.navigate(
                                Destination.Recipes.createRoute(
                                    categoryId,
                                    categoryTitle
                                )
                            )
                        }
                    )
                }

                composable(Destination.Favorites.route) {
                    FavoritesScreen(
                        repository = RecipesRepository(),
                        favoritesManager = favoritesManager,
                        onRecipeClick = { recipeId ->
                            navController.navigate(
                                Destination.Details.createRoute(
                                    recipeId
                                )
                            )
                        })
                }

                composable(
                    route = Destination.Recipes.route,
                    arguments = listOf(
                        navArgument(Destination.CATEGORY_ID_ARG) { type = NavType.IntType },
                        navArgument(Destination.CATEGORY_TITLE_ARG) {
                            type = NavType.StringType
                            defaultValue = ""
                        }
                    )
                ) { backStackEntry ->
                    val categoryId =
                        backStackEntry.arguments?.getInt(Destination.CATEGORY_ID_ARG) ?: -1
                    val categoryTitle =
                        backStackEntry.arguments?.getString(Destination.CATEGORY_TITLE_ARG) ?: ""
                    RecipesScreen(
                        modifier = Modifier,
                        categoryId = categoryId,
                        categoryTitle = categoryTitle,
                        onRecipeClick = { recipeId, recipe ->
                            navController.navigate(Destination.Details.createRoute(recipeId))
                        }
                    )
                }

                composable(
                    route = Destination.Details.route,
                    arguments = listOf(
                        navArgument(Destination.RECIPE_ID_ARG) { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val recipeId = backStackEntry.arguments?.getInt(Destination.RECIPE_ID_ARG) ?: 0
                    val recipe = getRecipeByIdStub(recipeId)?.toUiModel()

                    if (recipe != null) RecipeDetailsRoute(recipe, favoritesManager)
                    else Text("Рецепт не найден")
                }
            }
        }
    }
}
