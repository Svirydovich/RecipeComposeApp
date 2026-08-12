package com.example.recipeapp.core.util

import android.content.Context
import androidx.core.content.edit

class FavoritePrefsManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isFavorite(recipeId: Int): Boolean =
        sharedPreferences.getStringSet(KEY_FAVORITE_RECIPE_IDS, emptySet())
            ?.contains(recipeId.toString()) == true

    fun addToFavorites(recipeId: Int) {
        val favorites = sharedPreferences.getStringSet(KEY_FAVORITE_RECIPE_IDS, emptySet())
        val updatedFavorites = favorites?.toMutableSet() ?: mutableSetOf()
        updatedFavorites.add(recipeId.toString())
        sharedPreferences.edit {
            putStringSet(KEY_FAVORITE_RECIPE_IDS, updatedFavorites)
        }
    }

    fun removeFromFavorites(recipeId: Int) {
        val favorites = sharedPreferences.getStringSet(KEY_FAVORITE_RECIPE_IDS, emptySet())
        val updatedFavorites = favorites?.toMutableSet() ?: mutableSetOf()
        updatedFavorites.remove(recipeId.toString())
        sharedPreferences.edit {
            putStringSet(KEY_FAVORITE_RECIPE_IDS, updatedFavorites)
        }
    }

    fun getAllFavorites(): Set<String> =
        sharedPreferences.getStringSet(KEY_FAVORITE_RECIPE_IDS, emptySet())?.toSet() ?: emptySet()

    companion object {
        const val PREFS_NAME = "recipe_app_prefs"
        const val KEY_FAVORITE_RECIPE_IDS = "favorite_recipe_ids"
    }
}
