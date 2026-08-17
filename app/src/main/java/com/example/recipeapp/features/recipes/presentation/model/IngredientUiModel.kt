package com.example.recipeapp.features.recipes.presentation.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.example.recipeapp.data.model.IngredientDto
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class IngredientUiModel(
    val name: String,
    val quantity: String,
    val unitOfMeasure: String
) : Parcelable

fun IngredientDto.toUiModel() = IngredientUiModel(
    name = description,
    quantity = quantity,
    unitOfMeasure = unitOfMeasure
)
