package com.mealmuse.data

import com.mealmuse.data.network.FoodRecipesApi
import com.mealmuse.models.FoodJoke
import com.mealmuse.models.FoodRecipe
import retrofit2.Response
import javax.inject.Inject

/**
 * La clase RemoteDataSource se encarga de realizar solicitudes HTTP a la API de recetas de alimentos utilizando la interfaz FoodRecipesApi.
 * Proporciona métodos para obtener recetas de alimentos, buscar recetas de alimentos y obtener una broma relacionada con la comida.
 */
class RemoteDataSource @Inject constructor(
    private val foodRecipesApi: FoodRecipesApi
){
    /**
     * Realiza una solicitud HTTP para obtener recetas de alimentos utilizando los parámetros de consulta proporcionados.
     * @param queries Mapa de parámetros de consulta.
     * @return Objeto Response que contiene la lista de recetas de alimentos.
     */
    suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipe> {
        return foodRecipesApi.getRecipes(queries)
    }

    /**
     * Realiza una solicitud HTTP para buscar recetas de alimentos utilizando los parámetros de búsqueda proporcionados.
     * @param searchQuery Mapa de parámetros de búsqueda.
     * @return Objeto Response que contiene la lista de recetas de alimentos.
     */
    suspend fun searchRecipes(searchQuery: Map<String, String>): Response<FoodRecipe> {
        return foodRecipesApi.searchRecipes(searchQuery)
    }

    /**
     * Realiza una solicitud HTTP para obtener una broma relacionada con la comida utilizando la clave de API proporcionada.
     * @param apiKey Clave de API requerida para realizar la solicitud.
     * @return Objeto Response que contiene la broma relacionada con la comida.
     */

    suspend fun getFoodJoke(apiKey: String): Response<FoodJoke> {
        return foodRecipesApi.getFoodJoke(apiKey)
    }
}