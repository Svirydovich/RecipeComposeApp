package com.example.recipeapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.recipeapp.ui.theme.RecipeAppTheme

@Composable
fun RecipesApp() {
    RecipeAppTheme {
        Scaffold { paddingValues ->
            Text(
                "Recipes App",
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}
