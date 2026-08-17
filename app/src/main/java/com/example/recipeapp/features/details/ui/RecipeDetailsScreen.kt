package com.example.recipeapp.features.details.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.recipeapp.core.ui.ScreenHeader
import com.example.recipeapp.core.util.FavoriteDataStoreManager
import com.example.recipeapp.core.util.shareRecipe
import com.example.recipeapp.data.repository.adjustIngredient
import com.example.recipeapp.features.recipes.presentation.model.RecipeUiModel
import com.example.recipeapp.ui.theme.Dimens
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun RecipeDetailsRoute(
    recipe: RecipeUiModel,
    favoritesManager: FavoriteDataStoreManager,
    modifier: Modifier = Modifier
) {
    var currentPortions by rememberSaveable { mutableIntStateOf(recipe.servings) }
    val isFavorite by remember(recipe.id) {
        favoritesManager.isFavoriteFlow(recipe.id)
    }.collectAsState(initial = false)

    val coroutineScope = rememberCoroutineScope()

    val onFavoriteToggle: () -> Unit = {
        coroutineScope.launch {
            if (isFavorite) favoritesManager.removeFavorite(recipe.id)
            else favoritesManager.addFavorite(recipe.id)
        }
    }

    RecipeDetailsScreen(
        recipe = recipe,
        isFavorite = isFavorite,
        onFavoriteToggle = onFavoriteToggle,
        currentPortions = currentPortions,
        onPortionsChange = { currentPortions = it },
        modifier = modifier
    )
}

@Composable
fun RecipeDetailsScreen(
    recipe: RecipeUiModel,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    currentPortions: Int,
    onPortionsChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var sliderValue by remember(currentPortions) { mutableFloatStateOf(currentPortions.toFloat()) }

    val adjustedIngredients = remember(recipe.ingredients, currentPortions, recipe.servings) {
        val multiplier = currentPortions.toFloat() / recipe.servings.toFloat()
        recipe.ingredients.map { adjustIngredient(it, multiplier) }
    }

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
            onShareClick = { shareRecipe(context, recipe.id, recipe.title) },
            showFavoriteButton = true,
            isFavorite = isFavorite,
            onFavoriteToggle = onFavoriteToggle
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.Padding.PaddingMain),
            horizontalArrangement = Arrangement.spacedBy(Dimens.Padding.PaddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Порции: $currentPortions",
                style = MaterialTheme.typography.bodyLarge
            )

            Slider(
                value = sliderValue,
                onValueChange = { newFloat ->
                    sliderValue = newFloat
                },
                onValueChangeFinished = {
                    val rounded = sliderValue.roundToInt().coerceIn(1, recipe.servings * 3)
                    if (rounded != currentPortions) {
                        onPortionsChange(rounded)
                    }
                },
                valueRange = 1f..(recipe.servings * 3).toFloat(),
                steps = 0
            )
        }

        adjustedIngredients.forEachIndexed { index, ingredient ->
            IngredientItem(ingredient = ingredient)
            if (index < adjustedIngredients.lastIndex) {
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
