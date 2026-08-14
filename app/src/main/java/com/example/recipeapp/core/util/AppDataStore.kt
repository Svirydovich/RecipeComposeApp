package com.example.recipeapp.core.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "recipe_app_prefs",
    produceMigrations = { context ->
        listOf(
            SharedPreferencesMigration(
                context = context,
                sharedPreferencesName = "FavoriteRecipes"
            )
        )
    }
)

object PreferencesKeys {
    val FAVORITE_RECIPE_IDS = stringSetPreferencesKey("favorite_recipe_ids")
}
