package com.example.recipeapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.recipeapp.core.ui.navigation.BottomNavigation
import com.example.recipeapp.ui.categories.CategoriesScreen
import com.example.recipeapp.ui.favorites.FavoritesScreen
import com.example.recipeapp.ui.recipes.RecipesScreen
import com.example.recipeapp.ui.theme.RecipeAppTheme

@Composable
fun RecipesApp() {
    RecipeAppTheme {
        var currentScreen by remember { mutableStateOf(ScreenId.CATEGORIES) }
        var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
        var selectedCategoryTitle by remember { mutableStateOf<String>("") }
        Scaffold(
            bottomBar = {
                BottomNavigation(
                    onCategoriesClick = { currentScreen = ScreenId.CATEGORIES },
                    onFavoriteClick = { currentScreen = ScreenId.FAVORITES }
                )
            }
        ) { paddingValues ->
            when (currentScreen) {
                ScreenId.CATEGORIES -> CategoriesScreen(
                    modifier = Modifier.padding(paddingValues),
                    onCategoryClick = { categoryId, categoryTitle ->
                        selectedCategoryId = categoryId
                        selectedCategoryTitle = categoryTitle
                        currentScreen = ScreenId.RECIPES
                    }
                )

                ScreenId.FAVORITES -> FavoritesScreen(Modifier.padding(paddingValues))

                ScreenId.RECIPES -> selectedCategoryId?.let { id ->
                    RecipesScreen(
                        modifier = Modifier.padding(paddingValues),
                        categoryId = id,
                        categoryTitle = selectedCategoryTitle
                    )
                }
            }
        }
    }
}
