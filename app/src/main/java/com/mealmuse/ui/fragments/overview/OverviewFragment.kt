package com.mealmuse.ui.fragments.overview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import coil.load
import com.mealmuse.R
import com.mealmuse.bindingadapters.RecipesRowBinding
import com.mealmuse.databinding.FragmentOverviewBinding
import com.mealmuse.util.retrieveParcelable
import com.mealmuse.util.Constants.Companion.RECIPE_RESULT_KEY

class OverviewFragment : Fragment() {

    private var _binding: FragmentOverviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)

        val args = arguments
        val myBundle: com.mealmuse.models.Result? =
            args!!.retrieveParcelable(RECIPE_RESULT_KEY) as com.mealmuse.models.Result?

        if (myBundle != null) {
            binding.mainImageView.load(myBundle.image)
            binding.titleTextView.text = myBundle.title
            binding.likesTextView.text = myBundle.aggregateLikes.toString()
            binding.timeTextView.text = myBundle.readyInMinutes.toString()
            RecipesRowBinding.parseHtml(binding.summaryTextView, myBundle.summary)

            updateColors(
                myBundle.vegetarian,
                binding.vegTextView,
                binding.vegImageView
            )
            updateColors(myBundle.vegan, binding.veganTextView, binding.veganImageView)
            updateColors(myBundle.cheap, binding.cheapTextView, binding.cheapImageView)
            updateColors(myBundle.dairyFree, binding.dairyTextView, binding.dairyImageView)
            updateColors(
                myBundle.glutenFree,
                binding.glutenTextView,
                binding.glutenImageView
            )
            updateColors(myBundle.veryHealthy, binding.healthyTextView, binding.healthyImageView)
        }

        return binding.root
    }


    private fun updateColors(stateIsOn: Boolean, textView: TextView, imageView: ImageView) {
        if (stateIsOn) {
            imageView.setColorFilter(ContextCompat.getColor(requireContext(),R.color.green))
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}