package com.example.recipeapp.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.recipeapp.ui.theme.Dimens

@Composable
fun ScreenHeader(
    imageModel: Any?,
    contentDescription: String,
    title: String,
    showShareButton: Boolean = false,
    onShareClick: () -> Unit = {},
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

        if (showShareButton) {
            Surface(
                onClick = onShareClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(
                        top = Dimens.Padding.PaddingMain,
                        end = Dimens.Padding.PaddingMain
                    ),
                color = Color.White.copy(alpha = 0.7f),
                shape = MaterialTheme.shapes.extraSmall,
                shadowElevation = Dimens.Elevation.Level1
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Поделиться рецептом",
                    tint = Color.Black,
                    modifier = Modifier
                        .padding(Dimens.Padding.PaddingSmall)
                        .size(Dimens.Button.IconSize)
                )
            }
        }
    }
}
