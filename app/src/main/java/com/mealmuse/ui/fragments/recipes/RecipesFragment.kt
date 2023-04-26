package com.mealmuse.ui.fragments.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mealmuse.viewmodels.MainViewModel
import com.mealmuse.R
import com.mealmuse.adapters.RecipesAdapter
import com.mealmuse.util.NetworkResult
import com.mealmuse.viewmodels.RecipesViewModel
import kotlinx.android.synthetic.main.fragment_recipes.view.*


class RecipesFragment : Fragment() {

    private lateinit var mView: View
    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel
    private val mAdapter by lazy { RecipesAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        recipesViewModel = ViewModelProvider(requireActivity()).get(RecipesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_recipes, container, false)

        //So whenever our application starts, our cycle of you will set up and this short treatment effect will
        //appear.
        setupRecyclerView()
        requestApiData()

        return mView
    }

    private fun requestApiData() {
        mainViewModel.getRecipes(recipesViewModel.applyQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner, { response ->
            when(response){
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let { mAdapter.setData(it) }
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading ->{
                    showShimmerEffect()
                }
            }
        })
    }



    private fun setupRecyclerView() {
        mView.recyclerview.adapter = mAdapter
        mView.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    //The showShimmerEffect() function uses the showShimmer() method of the list view (mView.recyclerview)
    // to show the shimmer effect.
    private fun showShimmerEffect() {
        mView.recyclerview.showShimmer()
    }

    //The hideShimmerEffect() function uses the hideShimmer() method of the list
    // view to hide the shimmer effect once the list items have
    //    // been loaded and are ready to be displayed.
    private fun hideShimmerEffect() {
        mView.recyclerview.hideShimmer()
    }

}