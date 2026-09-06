package com.example.recipeapp.data.database

import android.content.Context
import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import com.example.recipeapp.data.database.converter.Converters
import com.example.recipeapp.data.database.dao.CategoryDao
import com.example.recipeapp.data.database.dao.RecipeDao
import com.example.recipeapp.data.database.entity.CategoryEntity
import com.example.recipeapp.data.database.entity.RecipeEntity

@ColumnTypeConverters(Converters::class)
@Database(entities = [CategoryEntity::class, RecipeEntity::class], version = 2, exportSchema = false)
abstract class RecipesDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun recipeDao(): RecipeDao

    companion object {
        fun buildDatabase(context: Context): RecipesDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                RecipesDatabase::class.java,
                "recipes_database"
            ).fallbackToDestructiveMigration()
                .build()
    }
}
