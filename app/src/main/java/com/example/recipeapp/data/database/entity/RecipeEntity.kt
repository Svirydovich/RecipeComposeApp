package com.example.recipeapp.data.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

@Entity(
    tableName = "recipes",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RecipeEntity(
    @PrimaryKey val id: Int,
    val title: String,
    @ColumnInfo(name = "category_id") val categoryId: Int,
    val imageUrl: String,
    val ingredients: String,
    val method: String
)
