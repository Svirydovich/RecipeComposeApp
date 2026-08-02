package com.example.recipeapp.ui.details

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.recipeapp.ui.recipes.model.RecipeUiModel

@Composable
fun RecipeDetailsScreen(recipe: RecipeUiModel, modifier: Modifier = Modifier) {
    Text(
        text = recipe.title,
        style = MaterialTheme.typography.headlineMedium,
        modifier = modifier.padding(16.dp)
    )
}
