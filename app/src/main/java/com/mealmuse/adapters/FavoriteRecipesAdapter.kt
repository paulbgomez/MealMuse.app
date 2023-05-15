
/**
 * Adaptador para mostrar las recetas favoritas en un RecyclerView.
 *
 * @param requireActivity La FragmentActivity asociada al adaptador.
 * @param mainViewModel El MainViewModel utilizado para realizar acciones en las recetas favoritas.
 */

package com.mealmuse.adapters
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mealmuse.R
import com.mealmuse.data.database.entities.FavoritesEntity
import com.mealmuse.databinding.FavoriteRecipesRowLayoutBinding
import com.mealmuse.ui.fragments.favorites.FavoriteRecipesFragmentDirections
import com.mealmuse.util.RecipesDiffUtil
import com.mealmuse.viewmodels.MainViewModel


/**
 * Constructor de la clase FavoriteRecipesAdapter.
 *
 * @param requireActivity La FragmentActivity asociada al adaptador.
 * @param mainViewModel El MainViewModel utilizado para realizar acciones en las recetas favoritas.
 */
class FavoriteRecipesAdapter(
    private val requireActivity: FragmentActivity,
    private val mainViewModel: MainViewModel
) : RecyclerView.Adapter<FavoriteRecipesAdapter.MyViewHolder>(), ActionMode.Callback {

    private var multiSelection = false

    private lateinit var mActionMode: ActionMode
    private lateinit var rootView: View

    private var selectedRecipes = arrayListOf<FavoritesEntity>()
    private var myViewHolders = arrayListOf<MyViewHolder>()
    private var favoriteRecipes = emptyList<FavoritesEntity>()

    class MyViewHolder(val binding: FavoriteRecipesRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Vincula los datos de una receta favorita con la vista.
         *
         * @param favoritesEntity La entidad de la receta favorita.
         */
        fun bind(favoritesEntity: FavoritesEntity) {
            binding.favoritesEntity = favoritesEntity
            binding.executePendingBindings()
        }

        companion object {
            /**
             * Crea un ViewHolder a partir de un ViewGroup.
             *
             * @param parent El ViewGroup padre.
             * @return El ViewHolder creado.
             */
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FavoriteRecipesRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    @Override
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    @Override
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        myViewHolders.add(holder)
        rootView = holder.itemView.rootView

        val currentRecipe = favoriteRecipes[position]
        holder.bind(currentRecipe)

        saveItemStateOnScroll(currentRecipe, holder)

        /**
         * Listener para el clic simple.
         **/
        holder.binding.favoriteRecipesRowLayout.setOnClickListener {
            if (multiSelection) {
                applySelection(holder, currentRecipe)
            }
            else {
                val action =
                    FavoriteRecipesFragmentDirections.actionFavoriteRecipesFragmentToDetailsActivity(
                        currentRecipe.result
                    )
                holder.itemView.findNavController().navigate(action)
            }
        }

        /**
         * Listener para el clic largo.
         **/
        holder.binding.favoriteRecipesRowLayout.setOnLongClickListener {
            if (!multiSelection) {
                multiSelection = true
                requireActivity.startActionMode(this)
                applySelection(holder, currentRecipe)
                true
            } else {
                applySelection(holder, currentRecipe)
                true
            }

        }

    }

    /**
     * Guarda el estado de un elemento durante el desplazamiento del RecyclerView.
     *
     * @param currentRecipe La entidad de la receta actual.
     * @param holder El ViewHolder actual.
     */

    private fun saveItemStateOnScroll(currentRecipe: FavoritesEntity, holder: MyViewHolder){
        if (selectedRecipes.contains(currentRecipe)) {
            changeRecipeStyle(holder, R.color.cardBackgroundLightColor, R.color.colorPrimary)
        } else {
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
        }
    }

    /**
     * Aplica la selección a un elemento del adaptador.
     *
     * @param holder El ViewHolder actual.
     * @param currentRecipe La entidad de la receta actual.
     */

    private fun applySelection(holder: MyViewHolder, currentRecipe: FavoritesEntity) {
        if (selectedRecipes.contains(currentRecipe)) {
            selectedRecipes.remove(currentRecipe)
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
            applyActionModeTitle()
        } else {
            selectedRecipes.add(currentRecipe)
            changeRecipeStyle(holder, R.color.cardBackgroundLightColor, R.color.colorPrimary)
            applyActionModeTitle()
        }
    }

    /**
     * Cambia el estilo de una receta en el ViewHolder.
     *
     * @param holder El ViewHolder actual.
     * @param backgroundColor El color de fondo a aplicar.
     * @param strokeColor El color del borde a aplicar.
     */

    private fun changeRecipeStyle(holder: MyViewHolder, backgroundColor: Int, strokeColor: Int) {
        holder.binding.favoriteRecipesRowLayout.setBackgroundColor(
            ContextCompat.getColor(requireActivity, backgroundColor)
        )
        holder.binding.favoriteRowCardView.strokeColor =
            ContextCompat.getColor(requireActivity, strokeColor)
    }

    /**
     * Aplica el título del ActionMode en función de la cantidad de elementos seleccionados.
     */

    private fun applyActionModeTitle() {
        when (selectedRecipes.size) {
            0 -> {
                mActionMode.finish()
                multiSelection = false
            }
            1 -> {
                mActionMode.title = "${selectedRecipes.size} item selected"
            }
            else -> {
                mActionMode.title = "${selectedRecipes.size} items selected"
            }
        }
    }

    /**
     * Devuelve la cantidad de elementos en el adaptador.
     *
     * @return La cantidad de elementos en el adaptador.
     */
    @Override
    override fun getItemCount(): Int {
        return favoriteRecipes.size
    }

    /**
     * Se llama cuando se crea el ActionMode.
     *
     * @param actionMode El ActionMode creado.
     * @param menu El menú del ActionMode.
     * @return true si se debe mostrar el ActionMode, false de lo contrario.
     */
    @Override
    override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        actionMode?.menuInflater?.inflate(R.menu.favorite_contextual_menu, menu)
        mActionMode = actionMode!!
        applyStatusBarColor(R.color.contextualStatusBarColor)
        return true
    }

    /**
     * Se llama cuando el ActionMode necesita ser actualizado.
     *
     * @param actionMode El ActionMode actual.
     * @param menu El menú del ActionMode.
     * @return true si el ActionMode debe ser actualizado, false de lo contrario.
     */
    @Override
    override fun onPrepareActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    /**
     * Se llama cuando se selecciona un elemento del menú del ActionMode.
     *
     * @param actionMode El ActionMode actual.
     * @param menu El menú del ActionMode.
     * @return true si el evento ha sido consumido, false de lo contrario.
     */
    @Override
    override fun onActionItemClicked(actionMode: ActionMode?, menu: MenuItem?): Boolean {
        if (menu?.itemId == R.id.delete_favorite_recipe_menu) {
            selectedRecipes.forEach {
                mainViewModel.deleteFavoriteRecipe(it)
            }
            showSnackBar("${selectedRecipes.size} Recipe/s removed.")

            multiSelection = false
            selectedRecipes.clear()
            actionMode?.finish()
        }
        return true
    }

    /**
     * Restaura el estado de los elementos cuando se destruye el ActionMode.
     *
     * @param actionMode El ActionMode actual.
     */
    @Override
    override fun onDestroyActionMode(actionMode: ActionMode?) {
        myViewHolders.forEach { holder ->
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
        }
        multiSelection = false
        selectedRecipes.clear()
        applyStatusBarColor(R.color.statusBarColor)
    }

    /**
     * Aplica el color de la barra de estado.
     *
     * @param color El color a aplicar.
     */
    private fun applyStatusBarColor(color: Int) {
        requireActivity.window.statusBarColor =
            ContextCompat.getColor(requireActivity, color)
    }

    /**
     * Establece los nuevos datos en el adaptador.
     *
     * @param newFavoriteRecipes La nueva lista de entidades de recetas favoritas.
     */
    fun setData(newFavoriteRecipes: List<FavoritesEntity>) {
        val favoriteRecipesDiffUtil =
            RecipesDiffUtil(favoriteRecipes, newFavoriteRecipes)
        val diffUtilResult = DiffUtil.calculateDiff(favoriteRecipesDiffUtil)
        favoriteRecipes = newFavoriteRecipes
        diffUtilResult.dispatchUpdatesTo(this)
    }

    /**
     * Muestra una Snackbar con un mensaje.
     *
     * @param message El mensaje a mostrar.
     */
    private fun showSnackBar(message: String) {
        Snackbar.make(
            rootView,
            message,
            Snackbar.LENGTH_SHORT
        ).setAction("Okay") {}
            .show()
    }

    /**
     * Limpia el ActionMode contextual.
     */
    fun clearContextualActionMode() {
        if (this::mActionMode.isInitialized) {
            mActionMode.finish()
        }
    }

}