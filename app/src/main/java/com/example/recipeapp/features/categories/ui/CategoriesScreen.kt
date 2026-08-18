package com.example.recipeapp.features.categories.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipeapp.R
import com.example.recipeapp.core.ui.ScreenHeader
import com.example.recipeapp.features.categories.presentation.CategoriesViewModel
import com.example.recipeapp.ui.theme.Dimens

@Composable
fun CategoriesScreen(modifier: Modifier = Modifier, onCategoryClick: (Int, String) -> Unit) {
    val viewModel: CategoriesViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val categories = uiState.categories

    Column(modifier = modifier.fillMaxSize()) {
        ScreenHeader(
            imageModel = R.drawable.ic_launcher_background,
            contentDescription = "Фоновое изображение категории",
            title = "Категории"
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(Dimens.Padding.PaddingMain),
            horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing.Medium),
            verticalArrangement = Arrangement.spacedBy(Dimens.Spacing.Medium),
            modifier = Modifier.fillMaxSize()
        ) {
            items(categories, key = { it.id }) { category ->
                CategoryItem(
                    category = category,
                    onClick = { onCategoryClick(category.id, category.title) }
                )
            }
        }
    }
}
