package com.example.recipeapp.data.model

import com.example.recipeapp.data.database.entity.CategoryEntity
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: Int,
    val title: String,
    val description: String? = null,
    val imageUrl: String? = null
)

fun CategoryDto.toEntity() = CategoryEntity(
    id = id,
    name = title,
    description = description,
    imageUrl = imageUrl
)

fun CategoryEntity.toDto() = CategoryDto(
    id = id,
    title = name,
    description = description,
    imageUrl = imageUrl
)
