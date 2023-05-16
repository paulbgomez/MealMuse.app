package com.mealmuse.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mealmuse.models.FoodRecipe


/**
 * Convertidor de tipos para la base de datos de recetas.
 *
 * Esta clase se utiliza para convertir los objetos de tipo FoodRecipe y Result en cadenas de texto
 * y viceversa, para que puedan ser almacenados y recuperados de la base de datos.
 */
class RecipesTypeConverter {

    var gson = Gson()

    /**
     * Convierte un objeto FoodRecipe en una cadena de texto.
     *
     * @param foodRecipe El objeto FoodRecipe a convertir.
     * @return La cadena de texto resultante.
     */
    @TypeConverter
    fun foodRecipeToString(foodRecipe: FoodRecipe): String {
        return gson.toJson(foodRecipe)
    }

    /**
     * Convierte una cadena de texto en un objeto FoodRecipe.
     *
     * @param data La cadena de texto a convertir.
     * @return El objeto FoodRecipe resultante.
     */
    @TypeConverter
    fun stringToFoodRecipe(data: String): FoodRecipe {
        val listType = object : TypeToken<FoodRecipe>() {}.type
        return gson.fromJson(data, listType)
    }

    /**
     * Convierte un objeto Result en una cadena de texto.
     *
     * @param result El objeto Result a convertir.
     * @return La cadena de texto resultante.
     */
    @TypeConverter
    fun resultToString(result: com.mealmuse.models.Result): String {
        return gson.toJson(result)
    }

    /**
     * Convierte una cadena de texto en un objeto Result.
     *
     * @param data La cadena de texto a convertir.
     * @return El objeto Result resultante.
     */
    @TypeConverter
    fun stringToResult(data: String): com.mealmuse.models.Result {
        val listType = object : TypeToken<com.mealmuse.models.Result>() {}.type
        return gson.fromJson(data, listType)
    }
}