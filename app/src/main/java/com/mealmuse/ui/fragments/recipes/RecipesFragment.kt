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

/**
 * Fragmento que muestra una lista de recetas.
 */
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

    /**
     * Se reanuda el fragmento y se restaura el estado del RecyclerView.
     */
    override fun onResume() {
        super.onResume()
        if (mainViewModel.recyclerViewState != null) {
            binding.recyclerview.layoutManager?.onRestoreInstanceState(mainViewModel.recyclerViewState)
        }
    }

    /**
     * Se crea el fragmento y se inicializan los ViewModels.
     *
     * @param savedInstanceState El estado previamente guardado del fragmento, si existe.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    /**
     * Infla el diseño de este fragmento, configura el menú y el RecyclerView, y establece observadores.
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
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            /**
             * Crea el menú y establece el listener de búsqueda.
             *
             * @param menu El menú que se va a crear.
             * @param menuInflater El inflater utilizado para inflar el menú.
             */
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.recipes_menu, menu)

                val search = menu.findItem(R.id.menu_search)
                val searchView = search.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@RecipesFragment)
            }

            /**
             * Se llama cuando se selecciona un elemento del menú.
             *
             * @param menuItem El elemento de menú seleccionado.
             * @return Booleano que indica si el evento de selección se ha consumido.
             */
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

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

    /**
     * Configura el RecyclerView con el adaptador y el administrador de diseño.
     */
    private fun setupRecyclerView() {
        binding.recyclerview.adapter = mAdapter
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    /**
     * Se llama cuando se envía una consulta de búsqueda.
     *
     * @param query La consulta de búsqueda enviada.
     * @return Booleano que indica si la consulta de búsqueda ha sido manejada.
     */
    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query != null) {
            searchApiData(query)
        }
        return true
    }

    /**
     * Se llama cuando el texto de la consulta de búsqueda cambia.
     *
     * @param p0 El nuevo texto de la consulta de búsqueda.
     * @return Booleano que indica si el cambio de texto de la consulta de búsqueda ha sido manejado.
     */
    override fun onQueryTextChange(p0: String?): Boolean {
        return true
    }



    /**
     * Lee datos de la base de datos local y los muestra en la interfaz de usuario.
     */
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

    /**
     * Realiza una solicitud a la API para obtener datos.
     */
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

    /**
     * Realiza una búsqueda en la API utilizando la consulta especificada.
     *
     * @param searchQuery La consulta de búsqueda.
     */
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


    /**
     * Carga datos desde la base de datos local y los muestra en la interfaz de usuario.
     */
    private fun loadDataFromCache() {
        mainViewModel.readRecipes.observe(viewLifecycleOwner) { database ->
            if (database.isNotEmpty()) {
                mAdapter.setData(database.first().foodRecipe)
            }
        }
    }

    /**
     * Muestra el efecto de shimmer en el RecyclerView.
     */
    private fun showShimmerEffect() {
        binding.shimmerFrameLayout.startShimmer()
        binding.shimmerFrameLayout.visibility = View.VISIBLE
        binding.recyclerview.visibility = View.GONE
    }

    /**
     * Oculta el efecto de shimmer en el RecyclerView.
     */
    private fun hideShimmerEffect() {
        binding.shimmerFrameLayout.stopShimmer()
        binding.shimmerFrameLayout.visibility = View.GONE
        binding.recyclerview.visibility = View.VISIBLE
    }

    /**
     * Se llama cuando la vista del fragmento se destruye.
     * Guarda el estado del RecyclerView en el ViewModel.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.recyclerViewState =
            binding.recyclerview.layoutManager?.onSaveInstanceState()
        _binding = null
    }

}