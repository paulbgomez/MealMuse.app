package com.mealmuse.models

import com.google.gson.annotations.SerializedName

/**
 * La clase FoodRecipe representa una receta de comida.
 */
data class FoodRecipe(
    @SerializedName("results")
    val results: List<Result>
)