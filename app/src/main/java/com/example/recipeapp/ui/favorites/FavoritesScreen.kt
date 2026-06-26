package com.example.recipeapp.ui.favorites

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
fun FavoritesScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(
            imagePainter = painterResource(R.drawable.bcg_favorites),
            contentDescription = "Фоновое изображение избранных рецептов",
            title = "Избранное"
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.Padding.PaddingMain),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Здесь будет список избранных рецептов")
        }
    }
}
