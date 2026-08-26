package com.example.recipeapp.features.favorites.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipeapp.R
import com.example.recipeapp.core.ui.ScreenHeader
import com.example.recipeapp.features.favorites.presentation.FavoritesViewModel
import com.example.recipeapp.features.favorites.presentation.FavoritesViewModelFactory
import com.example.recipeapp.features.favorites.presentation.model.FavoritesUiState
import com.example.recipeapp.features.recipes.ui.RecipeItem
import com.example.recipeapp.ui.theme.Dimens

@Composable
fun FavoritesRoute(
    onRecipeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: FavoritesViewModel = viewModel(factory = FavoritesViewModelFactory)
    val uiState by viewModel.uiState.collectAsState()
    FavoritesScreen(uiState = uiState, onRecipeClick = onRecipeClick, modifier = modifier)
}

@Composable
fun FavoritesScreen(
    uiState: FavoritesUiState,
    onRecipeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
        ScreenHeader(
            imageModel = R.drawable.bcg_favorites,
            contentDescription = "Фоновое изображение избранных рецептов",
            title = "Избранное"
        )

        when (uiState) {
            is FavoritesUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is FavoritesUiState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(Dimens.Padding.PaddingMain),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "В избранном пока пусто")
                }
            }

            is FavoritesUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(Dimens.Padding.PaddingMain),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is FavoritesUiState.Success -> {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(
                        items = uiState.recipes,
                        key = { it.id }
                    ) { recipe ->
                        RecipeItem(
                            recipe = recipe,
                            onRecipeClick = onRecipeClick,
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
}
