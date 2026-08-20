package com.example.recipeapp.navigation

import android.net.Uri

sealed class Destination(val route: String) {
    object Categories : Destination("categories")
    object Favorites : Destination("favorites")
    object Recipes :
        Destination("recipes/{$CATEGORY_ID_ARG}?$CATEGORY_TITLE_ARG={$CATEGORY_TITLE_ARG}&$CATEGORY_IMAGE_ARG={$CATEGORY_IMAGE_ARG}") {
        fun createRoute(categoryId: Int, categoryTitle: String, categoryImageUrl: String) =
            "recipes/$categoryId?$CATEGORY_TITLE_ARG=${Uri.encode(categoryTitle)}&$CATEGORY_IMAGE_ARG=${
                Uri.encode(
                    categoryImageUrl
                )
            }"
    }

    object Details : Destination("recipe/{$RECIPE_ID_ARG}") {
        fun createRoute(recipeId: Int) = "recipe/$recipeId"
        fun createRecipeDeepLink(recipeId: Int) = "$DEEP_LINK_BASE_URL/recipe/$recipeId"
    }

    companion object {
        const val CATEGORY_ID_ARG = "categoryId"
        const val CATEGORY_TITLE_ARG = "categoryTitle"
        const val RECIPE_ID_ARG = "recipeId"
        const val CATEGORY_IMAGE_ARG = "categoryImageUrl"
        const val DEEP_LINK_SCHEME = "recipeapp"
        const val DEEP_LINK_BASE_URL = "https://recipes.androidsprint.ru"
    }
}
