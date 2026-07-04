package com.example.recipeapp.ui.recipes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.recipeapp.R
import com.example.recipeapp.core.ui.ScreenHeader
import com.example.recipeapp.data.repository.getRecipesByCategoryId
import com.example.recipeapp.ui.recipes.model.RecipeUiModel
import com.example.recipeapp.ui.recipes.model.toUiModel
import com.example.recipeapp.ui.theme.Dimens

@Composable
fun RecipesScreen(
    modifier: Modifier = Modifier,
    categoryId: Int?,
    categoryTitle: String = "",
    onRecipeClick: (Int) -> Unit = {}
) {
    var recipes by remember { mutableStateOf<List<RecipeUiModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(categoryId) {
        categoryId?.let { safeId ->
            isLoading = true
            try {
                recipes = getRecipesByCategoryId(safeId).map { it.toUiModel() }
            } finally {
                isLoading = false
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        ScreenHeader(
            imagePainter = painterResource(R.drawable.bcg_recipes_list),
            contentDescription = "Фоновое изображение списка рецептов",
            title = categoryTitle
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                items(recipes, key = { it.id }) { recipe ->
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
