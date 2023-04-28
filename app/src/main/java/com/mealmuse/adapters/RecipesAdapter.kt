package com.mealmuse.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mealmuse.databinding.RecipesRowLayoutBinding
import com.mealmuse.models.FoodRecipe
import com.mealmuse.util.RecipesDiffUtil

class RecipesAdapter :RecyclerView.Adapter<RecipesAdapter.MyViewHolder>() {

    private var recipes = emptyList<com.mealmuse.models.Result>()


    class MyViewHolder(private val binding: RecipesRowLayoutBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(result: com.mealmuse.models.Result){
            binding.result = result
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecipesRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentRecipe = recipes[position]
        holder.bind(currentRecipe)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    //So whenever we call this set data function from our recipes fragment in the future, every time this
    //and if you will calculate the difference between our old list and the new list of data which we're going
    //to receive from this function, and it will update only those of yours which are not the same.
    fun setData(newData: FoodRecipe){
       val recipesDiffUtil =
           RecipesDiffUtil(recipes, newData.results)
       val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        recipes = newData.results
        diffUtilResult.dispatchUpdatesTo(this)

    }
}