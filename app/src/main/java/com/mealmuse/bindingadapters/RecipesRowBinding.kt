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

class RecipesRowBinding {

    //we need to add a companion object so we can access the functions inside this companion object
    //in the project.

    companion object {

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

        @BindingAdapter("loadImageFromUrl")
        @JvmStatic
        fun loadImageFromUrl(imageView: ImageView, imageUrl: String) {
            //when the image is loading or when it is loaded, it will have a fade in animation
            //of six hundred milliseconds and we will see that effect when we actually start fetching some API data.
            imageView.load(imageUrl) {
                crossfade(600)
                error(R.drawable.ic_error_placeholder)
            }
        }


        @BindingAdapter("applyVeganColor")
        @JvmStatic
        //first parameter will be a view and we are adding a view because we're going to use this
        //one both TextView and image view.
        //And the second parameter will be a boolean value so we can offer type boolean.
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