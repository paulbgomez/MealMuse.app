package com.mealmuse.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mealmuse.R
import com.mealmuse.databinding.IngredientsRowLayoutBinding
import com.mealmuse.models.ExtendedIngredient
import com.mealmuse.util.RecipesDiffUtil
import com.mealmuse.util.Constants.Companion.BASE_IMAGE_URL
import java.util.*

class IngredientsAdapter : RecyclerView.Adapter<IngredientsAdapter.MyViewHolder>() {

    // Lista de ingredientes
    private var ingredientsList = emptyList<ExtendedIngredient>()

    // Clase ViewHolder para almacenar referencias de vista
    class MyViewHolder(val binding: IngredientsRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    // Crea el ViewHolder rellenando el diseño del elemento de fila y devuelve el ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            IngredientsRowLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    // Vincula los datos del ingrediente a la vista del elemento de fila
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Carga la imagen del ingrediente
        holder.binding.ingredientImageView.load(BASE_IMAGE_URL + ingredientsList[position].image) {
            crossfade(600)
            error(R.drawable.ic_error_placeholder)
        }
        // Establece el nombre del ingrediente en la vista del elemento de fila
        holder.binding.ingredientName.text = ingredientsList[position].name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.ROOT
            ) else it.toString()
        }
        // Establece la cantidad del ingrediente en la vista del elemento de fila
        holder.binding.ingredientAmount.text = ingredientsList[position].amount.toString()
        // Establece la unidad del ingrediente en la vista del elemento de fila
        holder.binding.ingredientUnit.text = ingredientsList[position].unit
        // Establece la consistencia del ingrediente en la vista del elemento de fila
        holder.binding.ingredientConsistency.text = ingredientsList[position].consistency
        // Establece el texto original del ingrediente en la vista del elemento de fila
        holder.binding.ingredientOriginal.text = ingredientsList[position].original
    }

    // Devuelve la cantidad de elementos en la lista de ingredientes
    override fun getItemCount(): Int {
        return ingredientsList.size
    }

    // Establece los nuevos datos en la lista de ingredientes y actualiza la vista utilizando DiffUtil
    fun setData(newIngredients: List<ExtendedIngredient>) {
        // Crea un objeto RecipesDiffUtil para comparar las listas antiguas y nuevas
        val ingredientsDiffUtil =
            RecipesDiffUtil(ingredientsList, newIngredients)
        // Calcula la diferencia entre las listas antiguas y nuevas
        val diffUtilResult = DiffUtil.calculateDiff(ingredientsDiffUtil)
        // Establece la nueva lista de ingredientes
        ingredientsList = newIngredients
        // Actualiza la vista con la nueva lista utilizando DiffUtil
        diffUtilResult.dispatchUpdatesTo(this)
    }
}
