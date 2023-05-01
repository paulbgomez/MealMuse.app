package com.mealmuse.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mealmuse.data.DataStoreRepository
import com.mealmuse.util.Constants.Companion.API_KEY
import com.mealmuse.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.mealmuse.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.mealmuse.util.Constants.Companion.DEFAULT_RECIPES_NUMBER
import com.mealmuse.util.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.mealmuse.util.Constants.Companion.QUERY_API_KEY
import com.mealmuse.util.Constants.Companion.QUERY_DIET
import com.mealmuse.util.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.mealmuse.util.Constants.Companion.QUERY_NUMBER
import com.mealmuse.util.Constants.Companion.QUERY_TYPE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor (
    application: Application,
    private val dataStoreRepository: DataStoreRepository
) : AndroidViewModel(application){

    private var mealType = DEFAULT_MEAL_TYPE
    private var dietType = DEFAULT_DIET_TYPE

    val readMealAndDietType = dataStoreRepository.readMealAndDietType

    //se utiliza en la capa ViewModel de la arquitectura de la aplicación y se encarga de guardar los tipos de comida
    // y dieta seleccionados por el usuario en el DataStore de la aplicación.
    fun saveMealAndDietType(mealType: String, mealTypeId: Int, dietType: String, dietTypeId: Int) =
        //Dispatchers.IO se utiliza para realizar la operación de guardado en un hilo diferente al hilo principal, de esta forma se evita bloquear la interfaz de usuario mientras se realiza la operación de escritura en el DataStore.
        viewModelScope.launch(Dispatchers.IO) {
           //para acceder al DataStore y llamar a la función saveMealAndDietType definida en dicha clase. Esta función a su vez utiliza la instancia de
            // DataStore para editar los valores de preferencia correspondientes.
            dataStoreRepository.saveMealAndDietType(mealType, mealTypeId, dietType, dietTypeId)
        }



    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        // utiliza collect para obtener los valores almacenados en el DataStore a través de la instancia de DataStoreRepository.
        //Cuando se recibe un nuevo valor a través de collect, se asignan los valores de las variables selectedMealType y selectedDietType al mealType y dietType respectivamente.
        viewModelScope.launch {
            readMealAndDietType.collect { value ->
                mealType = value.selectedMealType
                dietType = value.selectedDietType
            }
        }

        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_TYPE] = mealType
        queries[QUERY_DIET] = dietType
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        return queries
    }

}