package com.example.recipeapp.features.recipes.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.recipeapp.R
import com.example.recipeapp.core.ui.ScreenHeader
import com.example.recipeapp.features.recipes.presentation.model.RecipeUiModel
import com.example.recipeapp.features.recipes.presentation.model.RecipesUiState
import com.example.recipeapp.ui.theme.Dimens

@Composable
fun RecipesScreen(
    uiState: RecipesUiState,
    onRecipeClick: (RecipeUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        ScreenHeader(
            imageModel = uiState.categoryImageUrl.takeIf { it.isNotEmpty() }
                ?: R.drawable.bcg_recipes_list,
            contentDescription = "Фоновое изображение списка рецептов",
            title = uiState.categoryTitle
        )

        when {
            uiState.isInitialLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.hasError -> Text(uiState.error.orEmpty())
            uiState.isEmpty -> Text("В этой категории пока нет рецептов")
            else -> LazyColumn {
                items(uiState.recipes, key = { it.id }) { recipe ->
                    RecipeItem(
                        recipe = recipe,
                        onRecipeClick = { onRecipeClick(recipe) },
                        modifier = Modifier.padding(
                            horizontal = Dimens.Padding.PaddingMain,
                            vertical = Dimens.Padding.PaddingMedium
                        )
                    )
                }
            }
        }
    }
}
