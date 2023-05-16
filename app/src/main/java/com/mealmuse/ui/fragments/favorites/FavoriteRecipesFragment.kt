package com.mealmuse.ui.fragments.favorites

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mealmuse.R
import com.mealmuse.adapters.FavoriteRecipesAdapter
import com.mealmuse.databinding.FragmentFavoriteRecipesBinding
import com.mealmuse.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * Fragmento para mostrar las recetas favoritas del usuario.
 */
@AndroidEntryPoint
class FavoriteRecipesFragment : Fragment() {

    private val mainViewModel: MainViewModel by viewModels()
    private val mAdapter: FavoriteRecipesAdapter by lazy {
        FavoriteRecipesAdapter(
            requireActivity(),
            mainViewModel
        )
    }

    private var _binding: FragmentFavoriteRecipesBinding? = null
    private val binding get() = _binding!!

    /**
     * Método que se llama al crear la vista del fragmento.
     * @param inflater El objeto LayoutInflater que se utiliza para inflar la vista del fragmento.
     * @param container El contenedor de la vista del fragmento.
     * @param savedInstanceState El estado previamente guardado del fragmento, si hay alguno.
     * @return La vista inflada del fragmento.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFavoriteRecipesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel
        binding.mAdapter = mAdapter

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            /**
             * Método para crear el menú del fragmento.
             * @param menu El objeto Menu al que se añadirán los elementos del menú.
             * @param menuInflater El objeto MenuInflater para inflar el menú.
             */
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.favorite_recipes_menu, menu)
            }
            /**
             * Método para manejar la selección de elementos de menú.
             * @param menuItem El elemento de menú seleccionado.
             * @return Booleano que indica si el evento de selección del elemento de menú ha sido consumido.
             */
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.deleteAll_favorite_recipes_menu) {
                    mainViewModel.deleteAllFavoriteRecipes()
                    showSnackBar()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        setupRecyclerView(binding.favoriteRecipesRecyclerView)

        return binding.root
    }

    /**
     * Método para configurar el RecyclerView.
     * @param recyclerView El RecyclerView a configurar.
     */
    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    /**
     * Método para mostrar una Snackbar.
     */
    private fun showSnackBar() {
        Snackbar.make(
            binding.root,
            "All recipes removed.",
            Snackbar.LENGTH_SHORT
        ).setAction("Okay") {}
            .show()
    }

    /**
     * Método que se llama cuando se destruye la vista del fragmento.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mAdapter.clearContextualActionMode()
    }
}