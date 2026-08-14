package com.example.recipeapp.core.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first

class FavoriteDataStoreManager(private val context: Context) {
    suspend fun isFavorite(recipeId: Int): Boolean {
        val preferences = context.dataStore.data.first()
        return preferences[PreferencesKeys.FAVORITE_RECIPE_IDS]?.contains(recipeId.toString()) ?: false
    }

    suspend fun addFavorite(recipeId: Int) {
        context.dataStore.edit { preferences ->
            val currentFavorites = preferences[PreferencesKeys.FAVORITE_RECIPE_IDS] ?: emptySet()
            val updatedFavorites = currentFavorites + recipeId.toString()
            preferences[PreferencesKeys.FAVORITE_RECIPE_IDS] = updatedFavorites
        }
    }

    suspend fun removeFavorite(recipeId: Int) {
        context.dataStore.edit { preferences ->
            val currentFavorites = preferences[PreferencesKeys.FAVORITE_RECIPE_IDS] ?: emptySet()
            val updatedFavorites = currentFavorites - recipeId.toString()
            preferences[PreferencesKeys.FAVORITE_RECIPE_IDS] = updatedFavorites
        }
    }
}
