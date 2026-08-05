package com.example.recipeapp.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.recipeapp.ui.theme.Dimens

@Composable
fun ScreenHeader(
    imageModel: Any?,
    contentDescription: String,
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.height(Dimens.Heights.HeaderHeight)
    ) {
        AsyncImage(
            model = imageModel,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Surface(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    horizontal = Dimens.Padding.PaddingMain,
                    vertical = Dimens.Padding.PaddingMedium
                )
        ) {
            Text(text = title)
        }
    }
}
