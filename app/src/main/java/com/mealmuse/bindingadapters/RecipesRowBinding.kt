package com.mealmuse.bindingadapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import coil.load
import com.mealmuse.R

class RecipesRowBinding {

    //we need to add a companion object so we can access the functions inside this companion object
    //in the project.

    companion object {

        @BindingAdapter("loadImageFromUrl")
        @JvmStatic
        fun loadImageFromUrl(imageView: ImageView, imageUrl: String) {
            //when the image is loading or when it is loaded, it will have a fade in animation
            //of six hundred milliseconds and we will see that effect when we actually start fetching some API data.
            imageView.load(imageUrl) {
                crossfade(600)
            }
        }

        //we need to specify the name of the attribute which we are going to use inside our recipes roll out.
        @BindingAdapter("setNumberOfLikes")
        //we are telling to the compiler to make the function static
        @JvmStatic
        //function which will convert integer value of number of likes to string.
        fun setNumberOfLikes(textView: TextView, likes: Int){
            textView.text = likes.toString()
        }

        @BindingAdapter("setNumberOfMinutes")
        @JvmStatic
        fun setNumberOfMinutes(textView: TextView, minutes: Int){
            textView.text = minutes.toString()
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

    }

}