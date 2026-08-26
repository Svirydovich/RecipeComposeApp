package com.example.recipeapp.features.details.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipeapp.core.ui.ScreenHeader
import com.example.recipeapp.core.util.shareRecipe
import com.example.recipeapp.features.details.presentation.RecipeDetailsViewModel
import com.example.recipeapp.features.details.presentation.RecipeDetailsViewModelFactory
import com.example.recipeapp.features.details.presentation.model.RecipeDetailsUiState
import com.example.recipeapp.features.recipes.presentation.model.RecipeUiModel
import com.example.recipeapp.ui.theme.Dimens
import kotlin.math.roundToInt

@Composable
fun RecipeDetailsRoute(
    recipe: RecipeUiModel,
    modifier: Modifier = Modifier
) {
    val viewModel: RecipeDetailsViewModel = viewModel(factory = RecipeDetailsViewModelFactory)
    LaunchedEffect(recipe.id) { viewModel.initializeWithRecipe(recipe) }

    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading || uiState.error != null -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(
                        text = uiState.error ?: "Неизвестная ошибка",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        uiState.recipe == null -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Данные рецепта отсутствуют")
            }
        }

        else -> {
            RecipeDetailsScreen(
                state = uiState,
                onFavoriteToggle = { viewModel.toggleFavorite() },
                onPortionsChange = { viewModel.updatePortions(it) },
                modifier = modifier
            )
        }
    }
}

@Composable
fun RecipeDetailsScreen(
    state: RecipeDetailsUiState,
    onFavoriteToggle: () -> Unit,
    onPortionsChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val recipe = state.recipe ?: return
    val context = LocalContext.current

    var sliderValue by remember(state.portions) { mutableFloatStateOf(state.portions.toFloat()) }

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
            isFavorite = state.isFavorite,
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
                text = "Порции: ${state.portions}",
                style = MaterialTheme.typography.bodyLarge
            )

            Slider(
                value = sliderValue,
                onValueChange = { newFloat ->
                    sliderValue = newFloat
                },
                onValueChangeFinished = {
                    val rounded = sliderValue.roundToInt().coerceIn(1, recipe.servings * 3)
                    if (rounded != state.portions) {
                        onPortionsChange(rounded)
                    }
                },
                valueRange = 1f..(recipe.servings * 3).toFloat(),
                steps = 0
            )
        }

        val scaledIngredients = state.scaledIngredients
        scaledIngredients.forEachIndexed { index, ingredient ->
            IngredientItem(ingredient = ingredient)
            if (index < scaledIngredients.lastIndex) {
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
