package com.example.recipeapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.recipeapp.ui.theme.RecipeAppTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.recipeapp.core.ui.navigation.BottomNavigation

@Composable
fun RecipesApp() {
    RecipeAppTheme {
        var currentScreen by remember { mutableStateOf(ScreenId.CATEGORIES) }
        Scaffold(
            bottomBar = {
                BottomNavigation(
                    onCategoriesClick = { currentScreen = ScreenId.CATEGORIES },
                    onFavoriteClick = { currentScreen = ScreenId.FAVORITES }
                )
            }
        ) { paddingValues ->
            when (currentScreen) {
                ScreenId.CATEGORIES -> {
                    Box(
                        modifier = Modifier.padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Категории")
                    }
                }

                ScreenId.FAVORITES -> {
                    Box(
                        modifier = Modifier.padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Избранное")
                    }
                }
            }
        }
    }
}
