package com.mealmuse.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mealmuse.databinding.RecipesRowLayoutBinding
import com.mealmuse.models.FoodRecipe
import com.mealmuse.util.RecipesDiffUtil
/**
 * Adaptador para mostrar recetas en un RecyclerView.
 */
class RecipesAdapter :RecyclerView.Adapter<RecipesAdapter.MyViewHolder>() {

    private var recipes = emptyList<com.mealmuse.models.Result>()


    /**
     * Crea un nuevo objeto MyViewHolder.
     *
     * @param binding El objeto de enlace para el diseño de la fila de recetas.
     */
    class MyViewHolder(private val binding: RecipesRowLayoutBinding):
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Vincula los datos de la receta al diseño de la fila.
         *
         * @param result El objeto Result que contiene los datos de la receta.
         */
        fun bind(result: com.mealmuse.models.Result){
            binding.result = result
            binding.executePendingBindings()
        }

        companion object {
            /**
             * Crea un objeto MyViewHolder a partir del diseño de la fila de recetas.
             *
             * @param parent El ViewGroup padre.
             * @return El objeto MyViewHolder creado.
             */
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecipesRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    /**
     * Crea un nuevo objeto MyViewHolder inflando el diseño de la fila de recetas.
     *
     * @param parent   El ViewGroup padre.
     * @param viewType El tipo de vista.
     * @return El objeto MyViewHolder creado.
     */
    @Override
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    /**
     * Vincula los datos de la receta al objeto MyViewHolder en la posición especificada.
     *
     * @param holder   El objeto MyViewHolder.
     * @param position La posición del elemento en el conjunto de datos.
     */
    @Override
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentRecipe = recipes[position]
        holder.bind(currentRecipe)
    }

    /**
     * Devuelve la cantidad de elementos en el conjunto de datos.
     *
     * @return La cantidad de recetas.
     */
    @Override
    override fun getItemCount(): Int {
        return recipes.size
    }


    /**
     * Establece los nuevos datos en el adaptador y actualiza la vista utilizando DiffUtil.
     *
     * @param newData El nuevo conjunto de datos de recetas.
     */
    fun setData(newData: FoodRecipe){
       val recipesDiffUtil =
           RecipesDiffUtil(recipes, newData.results)
       val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        recipes = newData.results
        diffUtilResult.dispatchUpdatesTo(this)

    }
}