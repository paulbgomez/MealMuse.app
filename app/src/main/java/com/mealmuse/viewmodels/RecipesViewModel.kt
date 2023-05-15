package com.mealmuse.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mealmuse.data.DataStoreRepository
import com.mealmuse.data.MealAndDietType
import com.mealmuse.util.Constants.Companion.API_KEY
import com.mealmuse.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.mealmuse.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.mealmuse.util.Constants.Companion.DEFAULT_RECIPES_NUMBER
import com.mealmuse.util.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.mealmuse.util.Constants.Companion.QUERY_API_KEY
import com.mealmuse.util.Constants.Companion.QUERY_DIET
import com.mealmuse.util.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.mealmuse.util.Constants.Companion.QUERY_NUMBER
import com.mealmuse.util.Constants.Companion.QUERY_SEARCH
import com.mealmuse.util.Constants.Companion.QUERY_TYPE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de recetas.
 *
 * @property application Referencia a la aplicación.
 * @property dataStoreRepository Repositorio de DataStore para almacenamiento de datos.
 */
@HiltViewModel
class RecipesViewModel @Inject constructor (
    application: Application,
    private val dataStoreRepository: DataStoreRepository
) : AndroidViewModel(application){

    private lateinit var mealAndDiet: MealAndDietType

    var networkStatus = false
    var backOnline = false

    val readMealAndDietType = dataStoreRepository.readMealAndDietType
    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()

    /**
     * Guarda el tipo de comida y dieta seleccionados en el DataStore.
     */
    fun saveMealAndDietType() =
        viewModelScope.launch(Dispatchers.IO) {
            if (this@RecipesViewModel::mealAndDiet.isInitialized) {
                dataStoreRepository.saveMealAndDietType(
                    mealAndDiet.selectedMealType,
                    mealAndDiet.selectedMealTypeId,
                    mealAndDiet.selectedDietType,
                    mealAndDiet.selectedDietTypeId
                )
            }
        }

    /**
     * Guarda temporalmente el tipo de comida y dieta seleccionados.
     *
     * @param mealType Tipo de comida seleccionado.
     * @param mealTypeId ID del tipo de comida seleccionado.
     * @param dietType Tipo de dieta seleccionado.
     * @param dietTypeId ID del tipo de dieta seleccionado.
     */
    fun saveMealAndDietTypeTemp(
        mealType: String,
        mealTypeId: Int,
        dietType: String,
        dietTypeId: Int
    ) {
        mealAndDiet = MealAndDietType(
            mealType,
            mealTypeId,
            dietType,
            dietTypeId
        )
    }

    /**
     * Guarda el estado de conexión a internet en el DataStore.
     *
     * @param backOnline Estado de conexión a internet.
     */
    private fun saveBackOnline(backOnline: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }

    /**
     * Aplica la consulta de búsqueda de recetas al crear un HashMap con los parámetros necesarios.
     *
     * @param searchQuery Consulta de búsqueda.
     * @return HashMap con los parámetros de la consulta.
     */
    fun applySearchQuery(searchQuery: String): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()
        queries[QUERY_SEARCH] = searchQuery
        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"
        return queries
    }

    /**
     * Aplica los parámetros de consulta para obtener recetas al crear un HashMap con los valores predeterminados
     * o los valores seleccionados por el usuario.
     *
     * @return HashMap con los parámetros de la consulta.
     */
    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        if (this@RecipesViewModel::mealAndDiet.isInitialized) {
            queries[QUERY_TYPE] = mealAndDiet.selectedMealType
            queries[QUERY_DIET] = mealAndDiet.selectedDietType
        } else {
            queries[QUERY_TYPE] = DEFAULT_MEAL_TYPE
            queries[QUERY_DIET] = DEFAULT_DIET_TYPE
        }

        return queries
    }

    /**
     * Muestra el estado de la conexión a internet y guarda el estado de conexión en el DataStore.
     */
    fun showNetworkStatus() {
        if (!networkStatus) {
            Toast.makeText(getApplication(), "No Internet Connection.", Toast.LENGTH_SHORT).show()
            saveBackOnline(true)
        } else if (networkStatus) {
            if (backOnline) {
                Toast.makeText(getApplication(), "We're back online.", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        }
    }



}