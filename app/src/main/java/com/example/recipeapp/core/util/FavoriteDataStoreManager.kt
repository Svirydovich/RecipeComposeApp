package com.example.recipeapp.core.util

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FavoriteDataStoreManager(private val context: Context) {
    suspend fun isFavorite(recipeId: Int): Boolean {
        val preferences = context.dataStore.data.first()
        return preferences[PreferencesKeys.FAVORITE_RECIPE_IDS]?.contains(recipeId.toString())
            ?: false
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

    fun getFavoriteIdsFlow(): Flow<Set<String>> {
        val preferencesFlow: Flow<Preferences> = context.dataStore.data
        return preferencesFlow.map { preferences ->
            preferences[PreferencesKeys.FAVORITE_RECIPE_IDS] ?: emptySet()
        }
    }

    fun isFavoriteFlow(recipeId: Int): Flow<Boolean> {
        return getFavoriteIdsFlow().map { favoriteIds ->
            favoriteIds.contains(recipeId.toString())
        }
    }

    fun getFavoriteCountFlow(): Flow<Int> {
        return getFavoriteIdsFlow().map { it.size }
    }
}
