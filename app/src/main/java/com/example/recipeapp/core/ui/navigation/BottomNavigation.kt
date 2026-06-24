package com.example.recipeapp.core.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.recipeapp.ui.theme.Dimens

@Composable
fun BottomNavigation(onCategoriesClick: () -> Unit, onFavoriteClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Padding.PaddingMain),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onCategoriesClick,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
            shape = RoundedCornerShape(Dimens.Padding.PaddingMedium)
        ) {
            Text("Категории")
        }
        Button(
            onClick = onFavoriteClick,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(Dimens.Padding.PaddingMedium)
        ) {
            Text("Избранное")
        }
    }
}
