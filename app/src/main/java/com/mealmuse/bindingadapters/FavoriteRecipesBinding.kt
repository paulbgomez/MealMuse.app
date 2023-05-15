package com.mealmuse.bindingadapters

import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mealmuse.adapters.FavoriteRecipesAdapter
import com.mealmuse.data.database.entities.FavoritesEntity

/**
 * Clase de enlace para las recetas favoritas.
 */
class FavoriteRecipesBinding {
    companion object {

        /**
         * Método de enlace para establecer la visibilidad y los datos de las recetas favoritas en una vista.
         *
         * @param view            La vista en la que se establecerá la visibilidad.
         * @param favoritesEntity La lista de entidades de recetas favoritas.
         * @param mAdapter        El adaptador de recetas favoritas.
         */
        @BindingAdapter("setVisibility", "setData", requireAll = false)
        @JvmStatic
        fun setVisibility(view: View, favoritesEntity: List<FavoritesEntity>?, mAdapter: FavoriteRecipesAdapter?) {
            when (view) {
                is RecyclerView -> {
                    val dataCheck = favoritesEntity.isNullOrEmpty()
                    view.isInvisible = dataCheck
                    if(!dataCheck){
                        favoritesEntity?.let { mAdapter?.setData(it) }
                    }
                }
                else -> view.isVisible = favoritesEntity.isNullOrEmpty()
            }
        }

    }

}