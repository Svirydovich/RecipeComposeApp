package com.example.recipeapp.ui.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.recipeapp.core.ui.ScreenHeader
import com.example.recipeapp.data.repository.getRecipeById
import com.example.recipeapp.ui.recipes.model.toUiModel
import com.example.recipeapp.ui.theme.Dimens

@Composable
fun RecipeDetailsScreen(recipeId: Int, modifier: Modifier = Modifier) {
    val uiRecipe = remember(recipeId) { getRecipeById(recipeId)?.toUiModel() }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ScreenHeader(
            imageModel = uiRecipe?.imageUrl,
            contentDescription = uiRecipe?.title ?: "",
            title = uiRecipe?.title ?: "Рецепт не найден"
        )

        uiRecipe?.ingredients?.forEachIndexed { index, ingredient ->
            IngredientItem(ingredient = ingredient)
            if (index < uiRecipe.ingredients.lastIndex) {
                HorizontalDivider()
            }
        }

        uiRecipe?.method?.forEach { step ->
            StepItem(step = step)
        }
    }
}

@Composable
fun StepItem(step: String, modifier: Modifier = Modifier) {
    Text(
        text = step,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = Dimens.Padding.PaddingMedium,
                horizontal = Dimens.Padding.PaddingMain
            )
    )
}
