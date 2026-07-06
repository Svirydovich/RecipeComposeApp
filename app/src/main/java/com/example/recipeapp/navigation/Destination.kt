package com.example.recipeapp.navigation

import android.net.Uri

sealed class Destination(val route: String) {
    object Categories : Destination("categories")
    object Favorites : Destination("favorites")
    object Recipes : Destination("recipes/{$CATEGORY_ID_ARG}?$CATEGORY_TITLE_ARG={$CATEGORY_TITLE_ARG}") {
        fun createRoute(categoryId: Int, categoryTitle: String) = "recipes/$categoryId?$CATEGORY_TITLE_ARG=${Uri.encode(categoryTitle)}"
    }
    companion object {
        const val CATEGORY_ID_ARG = "categoryId"
        const val CATEGORY_TITLE_ARG = "categoryTitle"
    }
}
