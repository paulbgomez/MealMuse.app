package com.mealmuse.ui.fragments.recipes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mealmuse.viewmodels.MainViewModel
import com.mealmuse.adapters.RecipesAdapter
import com.mealmuse.databinding.FragmentRecipesBinding
import com.mealmuse.util.NetworkResult
import com.mealmuse.util.observeOnce
import com.mealmuse.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RecipesFragment : Fragment() {

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel
    private val mAdapter by lazy { RecipesAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel
        //So whenever our application starts, our cycle of you will set up and this short treatment effect will
        //appear.
        setupRecyclerView()
        readDatabase()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerview.adapter = mAdapter
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    //La función readDatabase() se utiliza para leer datos de la base de datos local y mostrarlos en la interfaz de usuario de la aplicación.
    private fun readDatabase() {
        lifecycleScope.launch {
            // se llama a la función readRecipes del ViewModel (mainViewModel) para obtener la lista de entidades de recetas almacenadas en la base de datos. La función
            // observe se utiliza para observar los cambios en el objeto LiveData devuelto por la función readRecipes.
            mainViewModel.readRecipes.observeOnce(viewLifecycleOwner, { database ->
                if (database.isNotEmpty()) {
                    Log.d("RecipesFragment", "readDatabase called!")
                    //Cuando se detectan cambios en la lista de entidades de recetas (database), se verifica si la lista no está vacía y se llama a la función setData del adaptador (mAdapter) para establecer
                    // los datos en la lista de recetas que se mostrará en la interfaz de usuario.
                    mAdapter.setData(database[0].foodRecipe)
                    hideShimmerEffect()
                } else {
                    //Si la lista de entidades de recetas está vacía, se llama a la función requestApiData() para obtener los datos de una fuente remota (como una API).
                    requestApiData()
                }
            })
        }
    }

    private fun requestApiData() {
        Log.d("RecipesFragment", "requestApiData called!")
        mainViewModel.getRecipes(recipesViewModel.applyQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner, { response ->
            when(response){
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let { mAdapter.setData(it) }
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
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

    //La función loadDataFromCache() se utiliza para cargar datos de la base de datos local y mostrarlos en la interfaz de usuario de la aplicación.
    private fun loadDataFromCache() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observe(viewLifecycleOwner, {database->
                if (database.isNotEmpty()) {
                    mAdapter.setData(database[0].foodRecipe)
                }
            })
        }
    }


    //The showShimmerEffect() function uses the showShimmer() method of the list view (mView.recyclerview)
    // to show the shimmer effect.
    private fun showShimmerEffect() {
        binding.recyclerview.visibility = View.GONE
    }

    //The hideShimmerEffect() function uses the hideShimmer() method of the list
    // view to hide the shimmer effect once the list items have
    //    // been loaded and are ready to be displayed.
    private fun hideShimmerEffect() {
        binding.recyclerview.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}