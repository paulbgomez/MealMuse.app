package com.mealmuse.bindingadapters

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import coil.load
import com.mealmuse.R
import com.mealmuse.ui.fragments.recipes.RecipesFragmentDirections
import org.jsoup.Jsoup

/**
 * Clase de enlace para elementos de fila de recetas.
 */
class RecipesRowBinding {


    companion object {

        /**
         * Método de enlace para el evento de clic en una receta.
         *
         * @param recipeRowLayout    El diseño de la fila de la receta en el que se realizará el clic.
         * @param result             El objeto de resultado de la receta.
         */
        @BindingAdapter("onRecipeClickListener")
        @JvmStatic
        fun onRecipeClickListener(recipeRowLayout: ConstraintLayout, result: com.mealmuse.models.Result) {
            recipeRowLayout.setOnClickListener {
                try {
                    val action =
                        RecipesFragmentDirections.actionRecipesFragmentToDetailsActivity(result)
                    recipeRowLayout.findNavController().navigate(action)
                } catch (e: Exception) {
                    Log.d("onRecipeClickListener", e.toString())
                }
            }
        }

        /**
         * Método de enlace para cargar una imagen desde una URL en un ImageView.
         *
         * @param imageView    El ImageView en el que se cargará la imagen.
         * @param imageUrl     La URL de la imagen.
         */
        @BindingAdapter("loadImageFromUrl")
        @JvmStatic
        fun loadImageFromUrl(imageView: ImageView, imageUrl: String) {
            imageView.load(imageUrl) {
                crossfade(600)
                error(R.drawable.ic_error_placeholder)
            }
        }


        /**
         * Método de enlace para aplicar el color vegano a una vista.
         *
         * @param view    La vista a la que se aplicará el color vegano.
         * @param vegan   Booleano que indica si es vegano o no.
         */
        @BindingAdapter("applyVeganColor")
        @JvmStatic
        fun applyVeganColor(view: View, vegan: Boolean) {
            if(vegan){
                when(view){
                    is TextView -> {
                        view.setTextColor(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }
                    is ImageView -> {
                        view.setColorFilter(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }
                }
            }
        }

        /**
         * Método de enlace para analizar y mostrar texto HTML en un TextView.
         *
         * @param textView      El TextView en el que se mostrará el texto.
         * @param description   El texto en formato HTML a analizar y mostrar.
         */
        @BindingAdapter("parseHtml")
        @JvmStatic
        fun parseHtml(textView: TextView, description: String?){
            if(description != null) {
                val desc = Jsoup.parse(description).text()
                textView.text = desc
            }
        }

    }

}