package com.example.recipeapp.features.favorites.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.recipeapp.R
import com.example.recipeapp.core.ui.ScreenHeader
import com.example.recipeapp.core.util.FavoriteDataStoreManager
import com.example.recipeapp.data.repository.RecipesRepository
import com.example.recipeapp.features.recipes.ui.RecipeItem
import com.example.recipeapp.features.recipes.presentation.model.toUiModel
import kotlinx.coroutines.flow.map
import com.example.recipeapp.ui.theme.Dimens

@Composable
fun FavoritesScreen(
    repository: RecipesRepository,
    favoritesManager: FavoriteDataStoreManager,
    onRecipeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val favoriteRecipesFlow = remember(favoritesManager) {
        favoritesManager.getFavoriteIdsFlow()
            .map { ids ->
                ids.mapNotNull { it.toIntOrNull() }
                    .mapNotNull { id -> repository.getRecipeById(id) }
                    .map { it.toUiModel() }
            }
    }

    val favoriteRecipes by favoriteRecipesFlow.collectAsState(initial = emptyList())

    Column(modifier.fillMaxSize()) {
        ScreenHeader(
            imageModel = R.drawable.bcg_favorites,
            contentDescription = "Фоновое изображение избранных рецептов",
            title = "Избранное"
        )

        if (favoriteRecipes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimens.Padding.PaddingMain),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "В избранном пока пусто")
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(
                    items = favoriteRecipes,
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
