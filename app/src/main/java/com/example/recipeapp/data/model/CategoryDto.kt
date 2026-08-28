package com.example.recipeapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: Int,
    val title: String,
    val description: String? = null,
    val imageUrl: String? = null
)
