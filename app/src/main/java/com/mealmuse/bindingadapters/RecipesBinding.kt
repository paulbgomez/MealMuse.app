package com.mealmuse.bindingadapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.mealmuse.data.database.entities.RecipesEntity
import com.mealmuse.models.FoodRecipe
import com.mealmuse.util.NetworkResult


/**
 * Clase de enlace para recetas de comida.
 */
class RecipesBinding {

    companion object {

        /**
         * Método de enlace para manejar errores al leer los datos de las recetas.
         *
         * @param view          La vista en la que se realizarán las operaciones.
         * @param apiResponse   El resultado de la respuesta de la API de recetas.
         * @param database      La lista de entidades de recetas en la base de datos.
         */
        @BindingAdapter("readApiResponse","readDatabase",requireAll = true)
        @JvmStatic
        fun handleReadDataErrors(
            view: View,
            apiResponse: NetworkResult<FoodRecipe>?,
            database: List<RecipesEntity>?
        ){
            when (view){
                is ImageView ->{
                    view.isVisible = apiResponse is NetworkResult.Error && database.isNullOrEmpty()
                }
                is TextView ->{
                    view.isVisible = apiResponse is NetworkResult.Error && database.isNullOrEmpty()
                    view.text = apiResponse?.message.toString()
                }
            }
        }
    }

}