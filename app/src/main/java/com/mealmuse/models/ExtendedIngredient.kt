package com.mealmuse.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


/**
 * La clase ExtendedIngredient representa un ingrediente extendido de una receta.
 * Implementa la interfaz Parcelable para permitir el paso de objetos entre componentes de la aplicación.
 */
@Parcelize
data class ExtendedIngredient(
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("consistency")
    val consistency: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("original")
    val original: String,
    @SerializedName("unit")
    val unit: String
): Parcelable