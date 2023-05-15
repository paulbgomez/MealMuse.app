package com.mealmuse.ui.fragments.recipes

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mealmuse.R
import com.mealmuse.viewmodels.MainViewModel
import com.mealmuse.adapters.RecipesAdapter
import com.mealmuse.databinding.FragmentRecipesBinding
import com.mealmuse.util.NetworkListener
import com.mealmuse.util.NetworkResult
import com.mealmuse.util.observeOnce
import com.mealmuse.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RecipesFragment : Fragment(), SearchView.OnQueryTextListener {

    private val args by navArgs<RecipesFragmentArgs>()

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel
    private val mAdapter by lazy { RecipesAdapter() }

    private lateinit var networkListener: NetworkListener

    override fun onResume() {
        super.onResume()
        if (mainViewModel.recyclerViewState != null) {
            binding.recyclerview.layoutManager?.onRestoreInstanceState(mainViewModel.recyclerViewState)
        }
    }

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

        //está definiendo un menú en una actividad o fragmento y proporcionando una implementación de la interfaz MenuProvider para manejar la creación y selección de elementos del menú.
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.recipes_menu, menu)

                val search = menu.findItem(R.id.menu_search)
                val searchView = search.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@RecipesFragment)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        //So whenever our application starts, our cycle of you will set up and this short treatment effect will
        //appear.
        setupRecyclerView()

        //este código permite observar los cambios en el valor de disponibilidad de red (readBackOnline) del ViewModel y actualizar la propiedad backOnline del ViewModel en consecuencia. Esto es útil para asegurarse de que la aplicación tenga la información más actualizada sobre la disponibilidad de red en todo momento.
        recipesViewModel.readBackOnline.observe(viewLifecycleOwner, {
            recipesViewModel.backOnline = it
        })

        //el código está iniciando un observador de la disponibilidad de red utilizando NetworkListener y actualizando el estado de la red en el ViewModel cada vez que se produce un cambio en la disponibilidad de la red. Además, se está leyendo la base de datos cada vez que cambia el estado de la red.
       lifecycleScope.launch {
           networkListener = NetworkListener()
           networkListener.checkNetworkAvailability(requireContext())
               .collect { status ->
                    Log.d("NetworkListener", status.toString())
                    recipesViewModel.networkStatus = status
                    recipesViewModel.showNetworkStatus()
                    readDatabase()
           }
       }

        //este código está permitiendo al usuario acceder a un fragmento de la aplicación (recipesBottomSheet) si la red está disponible. Si no lo está, se muestra una alerta al usuario a través del método showNetworkStatus().
        binding.recipesFab.setOnClickListener {
            if (recipesViewModel.networkStatus) {
                findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)
            } else {
                recipesViewModel.showNetworkStatus()
            }
        }

        return binding.root
    }

    //este código es parte del proceso de configuración de RecyclerView y normalmente se llama durante la inicialización de un fragmento o actividad que muestra una lista de elementos
    private fun setupRecyclerView() {
        binding.recyclerview.adapter = mAdapter
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    //el método devuelve true para indicar que la consulta de búsqueda ha sido manejada.
    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query != null) {
            searchApiData(query)
        }
        return true
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        return true
    }



    //La función readDatabase() se utiliza para leer datos de la base de datos local y mostrarlos en la interfaz de usuario de la aplicación.
    private fun readDatabase() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observeOnce(viewLifecycleOwner) { database ->
                if (database.isNotEmpty() && !args.backFromBottomSheet) {
                    Log.d("RecipesFragment", "readDatabase called!")
                    mAdapter.setData(database.first().foodRecipe)
                    hideShimmerEffect()
                } else {
                    requestApiData()
                }
            }
        }
    }

    private fun requestApiData() {
        Log.d("RecipesFragment", "requestApiData called!")
        mainViewModel.getRecipes(recipesViewModel.applyQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let { mAdapter.setData(it) }
                    recipesViewModel.saveMealAndDietType()
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
                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        }
    }


    private fun searchApiData(searchQuery: String) {
        showShimmerEffect()
        mainViewModel.searchRecipes(recipesViewModel.applySearchQuery(searchQuery))
        mainViewModel.searchedRecipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    val foodRecipe = response.data
                    foodRecipe?.let { mAdapter.setData(it) }
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
                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        }
    }


    //La función loadDataFromCache() se utiliza para cargar datos de la base de datos local y mostrarlos en la interfaz de usuario de la aplicación.
    private fun loadDataFromCache() {
        mainViewModel.readRecipes.observe(viewLifecycleOwner) { database ->
            if (database.isNotEmpty()) {
                mAdapter.setData(database.first().foodRecipe)
            }
        }
    }

    //The showShimmerEffect() function uses the showShimmer() method of the list view (mView.recyclerview)
    // to show the shimmer effect.
    private fun showShimmerEffect() {
        binding.shimmerFrameLayout.startShimmer()
        binding.shimmerFrameLayout.visibility = View.VISIBLE
        binding.recyclerview.visibility = View.GONE
    }

    //The hideShimmerEffect() function uses the hideShimmer() method of the list
    // view to hide the shimmer effect once the list items have
    //    // been loaded and are ready to be displayed.
    private fun hideShimmerEffect() {
        binding.shimmerFrameLayout.stopShimmer()
        binding.shimmerFrameLayout.visibility = View.GONE
        binding.recyclerview.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.recyclerViewState =
            binding.recyclerview.layoutManager?.onSaveInstanceState()
        _binding = null
    }

}