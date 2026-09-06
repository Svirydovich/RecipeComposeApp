package com.example.recipeapp.data.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import com.example.recipeapp.data.database.entity.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): Flow<List<RecipeEntity>>
}
