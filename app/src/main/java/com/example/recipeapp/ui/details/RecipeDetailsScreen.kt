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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.recipeapp.core.ui.ScreenHeader
import com.example.recipeapp.core.util.shareRecipe
import com.example.recipeapp.data.model.RecipeDto
import com.example.recipeapp.ui.theme.Dimens

@Composable
fun RecipeDetailsScreen(
    recipe: RecipeDto,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ScreenHeader(
            imageModel = recipe.imageUrl,
            contentDescription = recipe.title,
            title = recipe.title,
            showShareButton = true,
            onShareClick = { shareRecipe(context, recipe.id, recipe.title) }
        )

        recipe.ingredients.forEachIndexed { index, ingredient ->
            IngredientItem(ingredient = ingredient)
            if (index < recipe.ingredients.lastIndex) {
                HorizontalDivider()
            }
        }

        recipe.method.forEachIndexed { index, step ->
            StepItem(stepNumber = index + 1, step = step)
        }
    }
}

@Composable
fun StepItem(stepNumber: Int, step: String, modifier: Modifier = Modifier) {
    Text(
        text = "$stepNumber. $step",
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = Dimens.Padding.PaddingMedium,
                horizontal = Dimens.Padding.PaddingMain
            )
    )
}
