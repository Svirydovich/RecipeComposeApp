package com.example.recipeapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColorDark,
    error = AccentColorDark,
    tertiary = AccentBlueDark,
    tertiaryContainer = SliderTrackColorDark,
    background = BackgroundColorDark,
    surface = SurfaceColorDark,
    outline = DividerColorDark,
    surfaceVariant = SurfaceVariantColorDark,

    onPrimary = TextPrimaryColorDark,
    onError = BackgroundColorDark,
    onBackground = TextSecondaryColorDark,
    onSurface = TextPrimaryColorDark,
    onSurfaceVariant = TextSecondaryColorDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    error = AccentColor,
    tertiary = AccentBlue,
    tertiaryContainer = SliderTrackColor,
    background = BackgroundColor,
    surface = SurfaceColor,
    outline = DividerColor,
    surfaceVariant = SurfaceVariantColor,

    onPrimary = BackgroundColor,
    onError = BackgroundColor,
    onBackground = TextPrimaryColor,
    onSurface = TextPrimaryColor,
    onSurfaceVariant = TextSecondaryColor
)

@Composable
fun RecipeAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = recipesAppTypography,
        content = content
    )
}
