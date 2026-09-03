package com.example.recipeapp.data.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import com.example.recipeapp.data.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name")
    fun getAllCategories(): Flow<List<CategoryEntity>>
}
