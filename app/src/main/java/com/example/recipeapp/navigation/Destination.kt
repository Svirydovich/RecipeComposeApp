package com.example.recipeapp.navigation

sealed class Destination(val route: String) {
    object Categories : Destination("categories")
    object Favorites : Destination("favorites")
    object Recipes : Destination("recipes/{categoryId}/{categoryTitle}") {
        fun createRoute(categoryId: Int, categoryTitle: String) = "recipes/$categoryId/$categoryTitle"
    }

    companion object {
        const val CATEGORY_ID_ARG = "categoryId"
        const val CATEGORY_TITLE_ARG = "categoryTitle"
    }
}
