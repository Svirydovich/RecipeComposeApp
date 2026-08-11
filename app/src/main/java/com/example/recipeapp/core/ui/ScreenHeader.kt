package com.example.recipeapp.core.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import coil.compose.AsyncImage
import com.example.recipeapp.R
import com.example.recipeapp.ui.theme.Dimens

@Composable
fun ScreenHeader(
    imageModel: Any?,
    contentDescription: String,
    title: String,
    showShareButton: Boolean = false,
    onShareClick: () -> Unit = {},
    showFavoriteButton: Boolean = false,
    isFavorite: Boolean = false,
    onFavoriteToggle: () -> Unit = {},
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

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = Dimens.Padding.PaddingMain, end = Dimens.Padding.PaddingMain),
            horizontalArrangement = Arrangement.spacedBy(Dimens.Padding.PaddingSmall)
        ) {
            if (showShareButton) {
                Surface(
                    onClick = onShareClick,
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
            if (showFavoriteButton) {
                Surface(
                    onClick = onFavoriteToggle,
                    color = Color.White.copy(alpha = 0.7f),
                    shape = MaterialTheme.shapes.extraSmall,
                    shadowElevation = Dimens.Elevation.Level1
                ) {
                    Crossfade(
                        targetState = isFavorite,
                        animationSpec = tween(durationMillis = 300),
                        label = "favorite_animation"
                    ) { isCurrentlyFavorite ->
                        val heartIcon = rememberVectorPainter(
                            image = ImageVector.vectorResource(
                                id = if (isCurrentlyFavorite) R.drawable.ic_heart else R.drawable.ic_heart_empty
                            )
                        )
                        Icon(
                            painter = heartIcon,
                            contentDescription = if (isCurrentlyFavorite) "Убрать из избранного" else "Добавить в избранное",
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .padding(Dimens.Padding.PaddingSmall)
                                .size(Dimens.Button.IconSize)
                        )
                    }
                }
            }
        }

    }
}
