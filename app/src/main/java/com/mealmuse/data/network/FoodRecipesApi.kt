package com.mealmuse.data.network

import com.mealmuse.models.FoodJoke
import com.mealmuse.models.FoodRecipe
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.Query
import retrofit2.http.QueryMap

//esta interfaz FoodRecipesApi define una solicitud HTTP para obtener recetas de alimentos utilizando la API complexSearch.
interface FoodRecipesApi {

    //este código permite realizar solicitudes HTTP a una API en línea de recetas de alimentos y recibir los resultados filtrados según las consultas proporcionadas.
    @GET("/recipes/complexSearch")
    suspend fun getRecipes(
        @QueryMap queries: Map<String, String>
    ): Response<FoodRecipe>

    //este código permite realizar solicitudes HTTP a una API de recetas de alimentos y recibir los resultados filtrados según las consultas proporcionadas en el mapa de searchQuery.
    @GET("/recipes/complexSearch")
    suspend fun searchRecipes(
        @QueryMap searchQuery: Map<String, String>
    ): Response<FoodRecipe>

    //La URL para la solicitud es "food/jokes/random" y se espera una respuesta que contenga un objeto de tipo FoodJoke.
    @GET("food/jokes/random")
    suspend fun getFoodJoke(
        @Query("apiKey") apiKey: String
    ): Response<FoodJoke>

}