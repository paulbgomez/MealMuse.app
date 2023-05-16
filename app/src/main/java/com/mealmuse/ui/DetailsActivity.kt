package com.mealmuse.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.mealmuse.R
import com.mealmuse.adapters.PagerAdapter
import com.mealmuse.data.database.entities.FavoritesEntity
import com.mealmuse.databinding.ActivityDetailsBinding
import com.mealmuse.ui.fragments.ingredients.IngredientsFragment
import com.mealmuse.ui.fragments.instructions.InstructionsFragment
import com.mealmuse.ui.fragments.overview.OverviewFragment
import com.mealmuse.util.Constants.Companion.RECIPE_RESULT_KEY
import com.mealmuse.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Actividad para mostrar los detalles de una receta.
 */
@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    private val args by navArgs<DetailsActivityArgs>()
    private val mainViewModel: MainViewModel by viewModels()

    private var recipeSaved = false
    private var savedRecipeId = 0

    private lateinit var menuItem: MenuItem

    /**
     * Método de ciclo de vida llamado cuando se crea la actividad.
     * @param savedInstanceState El estado previamente guardado de la actividad, o nulo si no hay ninguno.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragments = ArrayList<Fragment>()
        fragments.add(OverviewFragment())
        fragments.add(IngredientsFragment())
        fragments.add(InstructionsFragment())

        val titles = ArrayList<String>()
        titles.add("Overview")
        titles.add("Ingredients")
        titles.add("Instructions")

        val resultBundle = Bundle()
        resultBundle.putParcelable(RECIPE_RESULT_KEY, args.result)

        val pagerAdapter = PagerAdapter(
            resultBundle,
            fragments,
            this
        )
        binding.viewPager2.isUserInputEnabled = false
        binding.viewPager2.apply {
            adapter = pagerAdapter
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    /**
     * Método que se llama para crear el menú de opciones de la actividad.
     * @param menu El menú en el que se inflarán los elementos.
     * @return true si el menú se ha creado correctamente, false en caso contrario.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        menuItem = menu!!.findItem(R.id.save_to_favorites_menu)
        checkSavedRecipes(menuItem)
        return true
    }

    /**
     * Método que se llama cuando se selecciona un elemento del menú de opciones.
     * @param item El elemento del menú seleccionado.
     * @return true si se ha gestionado el evento correctamente, false en caso contrario.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else if (item.itemId == R.id.save_to_favorites_menu && !recipeSaved) {
            saveToFavorites(item)
        } else if (item.itemId == R.id.save_to_favorites_menu && recipeSaved) {
            removeFromFavorites(item)
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Método que verifica si las recetas están guardadas como favoritas y cambia el color del elemento del menú en consecuencia.
     * @param menuItem El elemento del menú que se va a verificar y modificar.
     */
    private fun checkSavedRecipes(menuItem: MenuItem) {
        mainViewModel.readFavoriteRecipes.observe(this) { favoritesEntity ->
            try {
                for (savedRecipe in favoritesEntity) {
                    if (savedRecipe.result.id == args.result.id) {
                        changeMenuItemColor(menuItem, R.color.yellow)
                        savedRecipeId = savedRecipe.id
                        recipeSaved = true
                    }
                }
            } catch (e: Exception) {
                Log.d("DetailsActivity", e.message.toString())
            }
        }
    }

    /**
     * Método que guarda la receta como favorita.
     * @param item El elemento del menú que representa la acción de guardar como favorita.
     */
    private fun saveToFavorites(item: MenuItem) {
        val favoritesEntity =
            FavoritesEntity(
                0,
                args.result
            )
        mainViewModel.insertFavoriteRecipe(favoritesEntity)
        changeMenuItemColor(item, R.color.yellow)
        showSnackBar("Recipe saved.")
        recipeSaved = true
    }

    /**
     * Método que elimina la receta de favoritos.
     * @param item El elemento del menú que representa la acción de eliminar de favoritos.
     */
    private fun removeFromFavorites(item: MenuItem) {
        val favoritesEntity =
            FavoritesEntity(
                savedRecipeId,
                args.result
            )
        mainViewModel.deleteFavoriteRecipe(favoritesEntity)
        changeMenuItemColor(item, R.color.white)
        showSnackBar("Removed from Favorites.")
        recipeSaved = false
    }

    /**
     * Método que muestra un SnackBar con un mensaje.
     * @param message El mensaje a mostrar en el SnackBar.
     */
    private fun showSnackBar(message: String) {
        Snackbar.make(
            binding.detailsLayout,
            message,
            Snackbar.LENGTH_SHORT
        ).setAction("Okay") {}
            .show()
    }

    /**
     * Método que cambia el color de un elemento del menú.
     * @param item El elemento del menú al que se le cambiará el color.
     * @param color El color a aplicar al elemento del menú.
     */
    private fun changeMenuItemColor(item: MenuItem, color: Int) {
        item.icon?.setTint(ContextCompat.getColor(this, color))
    }

    /**
     * Método de ciclo de vida llamado cuando la actividad está siendo destruida.
     */
    override fun onDestroy() {
        super.onDestroy()
        changeMenuItemColor(menuItem, R.color.white)
    }
}
