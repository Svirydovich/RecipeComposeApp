package com.example.recipeapp.data.model

import com.example.recipeapp.features.categories.presentation.model.toUiModel
import org.junit.Assert.*
import org.junit.Test

class CategoryDtoTest {
    @Test
    fun `converts DTO to UI model`() {
        val dto = CategoryDto(
            id = 1,
            title = "Завтраки",
            description = "Утренние блюда",
            imageUrl = "breakfast.jpg"
        )
        val result = dto.toUiModel()
        assertEquals("Завтраки", result.title)
    }
}
