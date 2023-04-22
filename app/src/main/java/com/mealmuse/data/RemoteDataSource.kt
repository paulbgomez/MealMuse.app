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
}