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

    @Test
    fun `mapper maps empty title correctly`() {
        val dto = CategoryDto(
            id = 2,
            title = "",
            description = "Описание без названия",
            imageUrl = null
        )

        val result = dto.toUiModel()

        assertEquals("", result.title)
        assertEquals("Описание без названия", result.description)
        assertEquals("", result.imageUrl)
    }

    @Test
    fun `mapper preserves very long description`() {
        val longDescription = buildString {
            append("Это очень длинное описание категории, которое содержит более двухсот символов для проверки того, ")
            append("что слой данных и последующий маппинг в презентационную модель не обрезают текстовое поле. ")
            append("Оно должно быть передано полностью, без потерь данных, усечения по длине или проблем с кодировкой.")
        }

        val dto = CategoryDto(
            id = 3,
            title = "Сложные рецепты",
            description = longDescription,
            imageUrl = "complex.jpg"
        )

        val result = dto.toUiModel()

        assertEquals(longDescription, result.description)
    }
}
