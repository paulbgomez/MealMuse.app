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
import com.mealmuse.models.Result
import com.mealmuse.util.retrieveParcelable
import com.mealmuse.util.Constants.Companion.RECIPE_RESULT_KEY

/**
 * Fragmento que muestra una vista general de una receta.
 */
class OverviewFragment : Fragment() {

    private var _binding: FragmentOverviewBinding? = null
    private val binding get() = _binding!!

    /**
     * Infla el diseño de este fragmento y muestra una vista general de la receta.
     *
     * @param inflater El inflater utilizado para inflar el diseño del fragmento.
     * @param container El contenedor padre en el cual el diseño del fragmento será colocado.
     * @param savedInstanceState El estado previamente guardado del fragmento, si existe.
     * @return La vista inflada del fragmento.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)

        val args = arguments
        val myBundle: Result? = args!!.retrieveParcelable(RECIPE_RESULT_KEY) as Result?

        if (myBundle != null) {
            binding.mainImageView.load(myBundle.image)
            binding.titleTextView.text = myBundle.title
            binding.likesTextView.text = myBundle.aggregateLikes.toString()
            binding.timeTextView.text = myBundle.readyInMinutes.toString()
            RecipesRowBinding.parseHtml(binding.summaryTextView, myBundle.summary)

            updateColors(myBundle.vegetarian, binding.vegTextView, binding.vegImageView)
            updateColors(myBundle.vegan, binding.veganTextView, binding.veganImageView)
            updateColors(myBundle.cheap, binding.cheapTextView, binding.cheapImageView)
            updateColors(myBundle.dairyFree, binding.dairyTextView, binding.dairyImageView)
            updateColors(myBundle.glutenFree, binding.glutenTextView, binding.glutenImageView)
            updateColors(myBundle.veryHealthy, binding.healthyTextView, binding.healthyImageView)
        }

        return binding.root
    }

    /**
     * Actualiza los colores de los elementos según el estado.
     *
     * @param stateIsOn Indica si el estado está activado o no.
     * @param textView El TextView que muestra el estado.
     * @param imageView El ImageView que muestra el estado.
     */
    private fun updateColors(stateIsOn: Boolean, textView: TextView, imageView: ImageView) {
        if (stateIsOn) {
            imageView.setColorFilter(ContextCompat.getColor(requireContext(),R.color.green))
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }
    }

    /**
     * Limpia el binding cuando el fragmento es destruido.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}