package com.example.recipeapp.data.model

import com.example.recipeapp.Constants
import com.example.recipeapp.features.recipes.presentation.model.toUiModel
import fixtures.RecipeTestFixtures.createIngredientDto
import org.junit.Assert.assertEquals
import org.junit.Test

class RecipeDtoMapperTest {
    @Test
    fun `maps DTO to UI model correctly`() {
        val dto = RecipeDto(
            id = 1,
            title = "Pasta Carbonara",
            ingredients = listOf(
                IngredientDto(
                    quantity = "200",
                    unitOfMeasure = "г",
                    description = "Паста"
                )
            ),
            method = listOf("Отварить пасту", "Смешать ингредиенты"),
            imageUrl = "pasta.jpg"
        )

        val result = dto.toUiModel()

        assertEquals(1, result.id)
        assertEquals("Pasta Carbonara", result.title)
        assertEquals(1, result.ingredients.size)
        with(result.ingredients[0]) {
            assertEquals("200", quantity)
            assertEquals("г", unitOfMeasure)
            assertEquals("Паста", name)
        }
        assertEquals(listOf("Отварить пасту", "Смешать ингредиенты"), result.method)
    }

    @Test
    fun `prepends base url to relative imageUrl`() {
        val dto = RecipeDto(
            id = 1,
            title = "Caesar salad",
            ingredients = listOf(createIngredientDto()),
            method = listOf("Нарезать", "Смешать"),
            imageUrl = "salad.jpg"
        )
        val result = dto.toUiModel()
        assertEquals(Constants.IMAGES_BASE_URL + "salad.jpg", result.imageUrl)
    }

    @Test
    fun `preserves full imageUrl starting with http`() {
        val remoteUrl = "https://cdn.example.com/images/remote-recipe.png"
        val dto = RecipeDto(
            id = 2,
            title = "Remote Image Dish",
            ingredients = emptyList(),
            method = emptyList(),
            imageUrl = remoteUrl
        )
        val result = dto.toUiModel()
        assertEquals(remoteUrl, result.imageUrl)
    }
}
