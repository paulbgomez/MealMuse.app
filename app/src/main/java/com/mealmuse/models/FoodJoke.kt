package com.mealmuse.models


import com.google.gson.annotations.SerializedName

/**
 * La clase FoodJoke representa una broma relacionada con la comida.
 */
data class FoodJoke(
    @SerializedName("text")
    val text: String
)