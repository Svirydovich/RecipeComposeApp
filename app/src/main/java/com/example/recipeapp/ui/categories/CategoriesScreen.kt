package com.example.recipeapp.ui.categories

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.recipeapp.R
import com.example.recipeapp.ui.components.ScreenHeader
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import com.example.recipeapp.ui.theme.Dimens

@Composable
fun CategoriesScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(
            imagePainter = painterResource(R.drawable.ic_launcher_background),
            contentDescription = "Фоновое изображение категорий",
            title = "Категории"
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.Padding.PaddingMain),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Здесь будет список категорий")
        }
    }
}
