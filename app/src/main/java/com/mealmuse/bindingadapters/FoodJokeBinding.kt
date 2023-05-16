package com.mealmuse.bindingadapters

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.card.MaterialCardView
import com.mealmuse.data.database.entities.FoodJokeEntity
import com.mealmuse.models.FoodJoke
import com.mealmuse.util.NetworkResult

/**
 * Clase de enlace para chistes de comida.
 */
class FoodJokeBinding {

    companion object {

        /**
         * Método de enlace para establecer la visibilidad de la tarjeta y la barra de progreso en función del estado de la respuesta de la API.
         *
         * @param view          La vista en la que se establecerá la visibilidad.
         * @param apiResponse   El resultado de la respuesta de la API.
         * @param database      La lista de entidades de chistes de comida en la base de datos.
         */
        @BindingAdapter("readApiResponse3", "readDatabase3", requireAll = false)
        @JvmStatic
        fun setCardAndProgressVisibility(
            view: View,
            apiResponse: NetworkResult<FoodJoke>?,
            database: List<FoodJokeEntity>?
        ) {
            when (apiResponse) {
                is NetworkResult.Loading -> {
                    when (view) {
                        is ProgressBar -> {
                            view.visibility = View.VISIBLE
                        }
                        is MaterialCardView -> {
                            view.visibility = View.INVISIBLE
                        }
                    }
                }
                is NetworkResult.Error -> {
                    when (view) {
                        is ProgressBar -> {
                            view.visibility = View.INVISIBLE
                        }
                        is MaterialCardView -> {
                            view.visibility = View.VISIBLE
                            if (database != null) {
                                if (database.isEmpty()) {
                                    view.visibility = View.INVISIBLE
                                }
                            }
                        }
                    }
                }
                is NetworkResult.Success -> {
                    when(view){
                        is ProgressBar -> {
                            view.visibility = View.INVISIBLE
                        }
                        is MaterialCardView -> {
                            view.visibility = View.VISIBLE
                        }
                    }
                }
                else -> {}
            }
        }

        /**
         * Método de enlace para establecer la visibilidad de las vistas de error en función del estado de la respuesta de la API y los datos de la base de datos.
         *
         * @param view          La vista en la que se establecerá la visibilidad.
         * @param apiResponse   El resultado de la respuesta de la API.
         * @param database      La lista de entidades de chistes de comida en la base de datos.
         */
        @BindingAdapter("readApiResponse4", "readDatabase4", requireAll = true)
        @JvmStatic
        fun setErrorViewsVisibility(
            view: View,
            apiResponse: NetworkResult<FoodJoke>?,
            database: List<FoodJokeEntity>?
        ){
            if(database != null){
                if(database.isEmpty()){
                    view.visibility = View.VISIBLE
                    if(view is TextView){
                        if(apiResponse != null){
                            view.text = apiResponse.message.toString()
                        }
                    }
                }
            }
            if(apiResponse is NetworkResult.Success){
                view.visibility = View.INVISIBLE
            }
        }

    }

}