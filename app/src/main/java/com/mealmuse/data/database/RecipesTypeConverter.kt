package com.mealmuse.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mealmuse.models.FoodRecipe

class RecipesTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun foodRecipeToString(foodRecipe: FoodRecipe): String {
        return gson.toJson(foodRecipe)
    }

    @TypeConverter
    fun stringToFoodRecipe(data: String): FoodRecipe {
        val listType = object : TypeToken<FoodRecipe>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun resultToString(result: com.mealmuse.models.Result): String {
        return gson.toJson(result)
    }

    @TypeConverter
    fun stringToResult(data: String): com.mealmuse.models.Result {
        val listType = object : TypeToken<com.mealmuse.models.Result>() {}.type
        return gson.fromJson(data, listType)
    }
}