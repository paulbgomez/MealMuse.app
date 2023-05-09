package com.mealmuse.models


import com.google.gson.annotations.SerializedName

//La anotación "@SerializedName("texto")" indica que la propiedad debe serializarse o deserializarse mediante el
// "texto" de la clave JSON, en lugar de utilizar el propio nombre de la propiedad.
data class FoodJoke(
    @SerializedName("text")
    val text: String
)