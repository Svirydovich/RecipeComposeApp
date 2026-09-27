package fixtures

import com.example.recipeapp.data.model.IngredientDto
import com.example.recipeapp.data.model.RecipeDto

object RecipeTestFixtures {

    fun createIngredientDto(
        quantity: String = "200",
        unitOfMeasure: String = "г",
        description: String = "Паста"
    ) = IngredientDto(quantity = quantity, unitOfMeasure = unitOfMeasure, description = description)

    fun createRecipeDto(
        id: Int = 1,
        title: String = "Pasta Carbonara",
        ingredients: List<IngredientDto> = listOf(createIngredientDto()),
        method: List<String> = listOf("Отварить", "Смешать"),
        imageUrl: String = "pasta.jpg"
    ) = RecipeDto(
        id = id,
        title = title,
        ingredients = ingredients,
        method = method,
        imageUrl = imageUrl
    )

    fun createRecipeDtoList(count: Int = 3) =
        List(count) { index -> createRecipeDto(id = index + 1, title = "Рецепт ${index + 1}") }
}
