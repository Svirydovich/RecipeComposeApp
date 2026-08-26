package com.example.recipeapp.features.recipes.presentation.model

import java.math.RoundingMode

fun Float.formatIngredientAmount(): String = this.toBigDecimal()
    .setScale(2, RoundingMode.HALF_UP)
    .stripTrailingZeros()
    .toPlainString()
