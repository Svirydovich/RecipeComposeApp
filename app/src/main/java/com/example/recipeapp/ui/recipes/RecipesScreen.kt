package com.example.recipeapp.ui.recipes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.recipeapp.R
import com.example.recipeapp.core.ui.ScreenHeader
import com.example.recipeapp.ui.theme.Dimens

@Composable
fun RecipesScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        ScreenHeader(
            imagePainter = painterResource(R.drawable.bcg_recipes_list),
            contentDescription = "Фоновое изображение списка рецептов",
            title = "Рецепты"
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.Padding.PaddingMain),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Скоро здесь будет список рецептов")
        }
    }
}
