package com.mealmuse.data

import com.mealmuse.data.network.FoodRecipesApi
import com.mealmuse.models.FoodRecipe
import retrofit2.Response
import javax.inject.Inject

/**
 * Especificamos el type que queremos inyectar en nuestro constructor -> FoodRecipesApi
 */
class RemoteDataSource @Inject constructor(
    private val foodRecipesApi: FoodRecipesApi
){
    /**
     * Anotamos esta funcion con suspend igual que nuestra funcion GET getRecipes (FoodRecipesApi.kt)
     * @param queries Las queries contra la API
     */
    suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipe> {
        return foodRecipesApi.getRecipes(queries)
    }

    //este código permite buscar recetas de alimentos utilizando la API complexSearch y devuelve los resultados filtrados según las consultas proporcionadas en el mapa de searchQuery.
    suspend fun searchRecipes(searchQuery: Map<String, String>): Response<FoodRecipe> {
        return foodRecipesApi.searchRecipes(searchQuery)
    }
}