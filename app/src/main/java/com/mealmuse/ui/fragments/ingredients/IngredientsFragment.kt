package com.mealmuse.ui.fragments.ingredients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mealmuse.adapters.IngredientsAdapter
import com.mealmuse.databinding.FragmentIngredientsBinding
import com.mealmuse.util.Constants.Companion.RECIPE_RESULT_KEY
import com.mealmuse.util.retrieveParcelable

class IngredientsFragment : Fragment() {

    // Inicializa los IngredientsAdapter usando lazy loading
    private val mAdapter: IngredientsAdapter by lazy { IngredientsAdapter() }

    // View binding
    private var _binding: FragmentIngredientsBinding? = null
    private val binding get() = _binding!!

    /**
     * Inflate the layout for this fragment and set up the RecyclerView
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIngredientsBinding.inflate(inflater, container, false)

        // Recuperar los detalles de la receta del bundle pasado desde la activity
        val args = arguments
        val myBundle: com.mealmuse.models.Result? = args?.retrieveParcelable(RECIPE_RESULT_KEY)

        // Set up el RecyclerView y lo popula con los ingredientes
        setupRecyclerView()
        myBundle?.extendedIngredients?.let { mAdapter.setData(it) }

        return binding.root
    }

    /**
     * Set up del RecyclerView
     */
    private fun setupRecyclerView() {
        binding.ingredientsRecyclerview.adapter = mAdapter
        binding.ingredientsRecyclerview.layoutManager = LinearLayoutManager(requireContext())
    }

    /**
     * Limpia el binding
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}