package com.mealmuse.data.network

import com.mealmuse.models.FoodJoke
import com.mealmuse.models.FoodRecipe
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 * Interfaz que define las solicitudes HTTP para obtener recetas de alimentos utilizando la API complexSearch.
 */
interface FoodRecipesApi {

    //este código permite realizar solicitudes HTTP a una API en línea de recetas de alimentos y recibir los resultados filtrados según las consultas proporcionadas.
    @GET("/recipes/complexSearch")
    suspend fun getRecipes(
        @QueryMap queries: Map<String, String>
    ): Response<FoodRecipe>

    /**
     * Realiza una solicitud HTTP para obtener recetas de alimentos filtradas según las consultas proporcionadas.
     *
     * @param queries Consultas para filtrar las recetas.
     * @return Respuesta que contiene el objeto FoodRecipe con las recetas obtenidas.
     */
    @GET("/recipes/complexSearch")
    suspend fun searchRecipes(
        @QueryMap searchQuery: Map<String, String>
    ): Response<FoodRecipe>

    /**
     * Realiza una solicitud HTTP para buscar recetas de alimentos según las consultas proporcionadas.
     *
     * @param searchQuery Consultas de búsqueda para filtrar las recetas.
     * @return Respuesta que contiene el objeto FoodRecipe con las recetas obtenidas.
     */
    @GET("food/jokes/random")
    suspend fun getFoodJoke(
        @Query("apiKey") apiKey: String
    ): Response<FoodJoke>

}