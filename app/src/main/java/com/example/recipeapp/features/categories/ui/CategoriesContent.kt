package com.example.recipeapp.features.categories.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.recipeapp.R
import com.example.recipeapp.core.ui.ScreenHeader
import com.example.recipeapp.features.categories.presentation.model.CategoriesUiState
import com.example.recipeapp.ui.theme.Dimens

@Composable
fun CategoriesContent(
    uiState: CategoriesUiState,
    onCategoryClick: (Int, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("categories_screen")
    ) {
        ScreenHeader(
            imageModel = R.drawable.bcg_categories,
            contentDescription = "Фоновое изображение категории",
            title = "Категории"
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.testTag("loading_indicator")
                    )
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error,
                        modifier = Modifier.testTag("error_message")
                    )
                }
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(Dimens.Padding.PaddingMain),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing.Medium),
                    verticalArrangement = Arrangement.spacedBy(Dimens.Spacing.Medium),
                    modifier = Modifier.fillMaxSize().testTag("categories_grid")
                ) {
                    items(
                        items = uiState.categories,
                        key = { it.id }
                    ) { category ->
                        CategoryItem(
                            category = category,
                            onClick = {
                                onCategoryClick(
                                    category.id,
                                    category.title,
                                    category.imageUrl
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
