package com.mealmuse.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Parcelable
import androidx.lifecycle.*
import com.mealmuse.data.Repository
import com.mealmuse.data.database.RecipesDatabase
import com.mealmuse.data.database.entities.FavoritesEntity
import com.mealmuse.data.database.entities.FoodJokeEntity
import com.mealmuse.data.database.entities.RecipesEntity
import com.mealmuse.models.FoodJoke
import com.mealmuse.models.FoodRecipe
import com.mealmuse.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject


/**
 * Clase MainViewModel que extiende ViewModel y se utiliza como ViewModel para la actividad principal.
 *
 * @param repository El repositorio que proporciona los datos de la aplicación.
 * @param application La instancia de la aplicación.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
): AndroidViewModel(application) {

    var recyclerViewState: Parcelable? = null

    /** ROOM DATABASE**/

    //Define un objeto LiveData que fluirá una lista de entidades de recetas obtenidas de la base de datos.
    // Utiliza la función readDatabase() de la clase LocalDataSource para acceder a los datos locales.
    // La función asLiveData() se utiliza para convertir el objeto Flow retornado por readDatabase() a un objeto LiveData.
    val readRecipes: LiveData<List<RecipesEntity>> = repository.local.readRecipes().asLiveData()
    val readFavoriteRecipes: LiveData<List<FavoritesEntity>> = repository.local.readFavoriteRecipes().asLiveData()
    val readFoodJoke: LiveData<List<FoodJokeEntity>> = repository.local.readFoodJoke().asLiveData()

    //Utiliza la función insertRecipes() de la clase LocalDataSource
    // para insertar una entidad de recetas en la base de datos. Esta función se ejecuta en
    // un viewModelScope y utiliza el Dispatchers.IO para realizar la operación de inserción en un hilo de fondo.
    private fun insertRecipes(recipesEntity: RecipesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertRecipes(recipesEntity)
        }

    // Esta función utiliza el objeto FavoritesEntity para insertar una receta favorita en la base de datos local.
    // Utiliza corutinas para ejecutar esta tarea en un subproceso de E/S.
    fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertFavoriteRecipes(favoritesEntity)
        }

    private fun insertFoodJoke(foodJokeEntity: FoodJokeEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertFoodJoke(foodJokeEntity)
        }


    // Esta función utiliza el objeto FavoritesEntity para eliminar una receta favorita de la base de datos local.
    // Utiliza corutinas para ejecutar esta tarea en un subproceso de E/S.
    fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteFavoriteRecipe(favoritesEntity)
        }

    // Esta función elimina todas las recetas favoritas de la base de datos local.
    // Utiliza corutinas para ejecutar esta tarea en un subproceso de E/S.
    fun deleteAllFavoriteRecipes() =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteAllFavoriteRecipes()
        }

    /**RETROFIT**/
    // Se declara una variable de LiveData llamada "recipesResponse" que contendrá los datos de la respuesta de la API
    var recipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    //esta variable se utiliza para almacenar y emitir los resultados de la búsqueda de recetas de alimentos en la aplicación, con información adicional de si la búsqueda tuvo éxito o no, errores que puedan haber ocurrido y el estado actual de la búsqueda
    var searchedRecipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var foodJokeResponse: MutableLiveData<NetworkResult<FoodJoke>> = MutableLiveData()


    /**
     * Función para obtener las recetas.
     *
     * @param queries Los parámetros de consulta para la API.
     */
    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queries) // Se llama a la función "getRecipesSafeCall" para realizar la llamada segura a la API
    }

    /**
     * Función para buscar recetas.
     *
     * @param searchQuery El parámetro de búsqueda para la API.
     */
    fun searchRecipes(searchQuery: Map<String, String>) = viewModelScope.launch {
        searchRecipesSafeCall(searchQuery)
    }

    /**
     * Función para obtener una broma relacionada con la comida.
     *
     * @param apiKey La clave de la API.
     */
    fun getFoodJoke(apiKey: String) = viewModelScope.launch {
        getFoodJokeSafeCall(apiKey)
    }

    /**
     * Función para realizar una llamada segura a la API y obtener una broma relacionada con la comida.
     *
     * @param apiKey La clave de la API.
     */
    private suspend fun getFoodJokeSafeCall(apiKey: String) {
        foodJokeResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getFoodJoke(apiKey)
                foodJokeResponse.value = handleFoodJokeResponse(response)

                val foodJoke = foodJokeResponse.value!!.data
                if(foodJoke != null){
                    offlineCacheFoodJoke(foodJoke)
                }
            } catch (e: Exception) {
                foodJokeResponse.value = NetworkResult.Error("Recipes not found.")
            }
        } else {
            foodJokeResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    /**
     * Función para realizar una llamada segura a la API y obtener las recetas de alimentos.
     *
     * @param queries Los parámetros de consulta para la búsqueda de recetas.
     */
    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        recipesResponse.value = NetworkResult.Loading()
        // Se comprueba si el dispositivo tiene conexión a internet utilizando la función "hasInternetConnection"
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getRecipes(queries)// Se llama a la función "getRecipes" de la clase Repository para obtener los datos de la receta
                // Se llama a la función "handleFoodRecipesResponse" para procesar la respuesta de la API y se establece el valor de "recipesResponse" con el resultado
                recipesResponse.value = handleFoodRecipesResponse(response)

                val foodRecipe = recipesResponse.value!!.data
                if(foodRecipe != null) {
                    offlineCacheRecipes(foodRecipe)
                }
            } catch (e: Exception) {
                recipesResponse.value = NetworkResult.Error("Recipes not found.")
                // Si se produce un error durante la llamada a la API, se establece un valor de error en "recipesResponse"
            }
        } else { // Si no hay conexión a internet, se establece un valor de error en "recipesResponse"
            recipesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    /**
     * Función para realizar una llamada segura a la API y buscar recetas de alimentos.
     *
     * @param searchQuery Los parámetros de búsqueda de recetas.
     */
    private suspend fun searchRecipesSafeCall(searchQuery: Map<String, String>) {
        searchedRecipesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.searchRecipes(searchQuery)
                searchedRecipesResponse.value = handleFoodRecipesResponse(response)
            } catch (e: Exception) {
                searchedRecipesResponse.value = NetworkResult.Error("Recipes not found.")
            }
        } else {
            searchedRecipesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    /**
     * Almacena en caché una receta de alimentos en la base de datos local.
     *
     * @param foodRecipe La receta de alimentos a almacenar en caché.
     */
    private fun offlineCacheRecipes(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipesEntity(foodRecipe)
        insertRecipes(recipesEntity)
    }

    /**
     * Almacena en caché una broma de alimentos en la base de datos local.
     *
     * @param foodJoke La broma de alimentos a almacenar en caché.
     */
    private fun offlineCacheFoodJoke(foodJoke: FoodJoke) {
        val foodJokeEntity = FoodJokeEntity(foodJoke)
        insertFoodJoke(foodJokeEntity)
    }

    /**
     * Maneja la respuesta de la API de recetas de alimentos y la convierte en un objeto NetworkResult.
     *
     * @param response La respuesta de la API de recetas de alimentos.
     * @return Un objeto NetworkResult que contiene el resultado de la respuesta.
     */
    private fun handleFoodRecipesResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe>? {
        // Se comprueba si la respuesta de la API contiene un mensaje de "timeout"
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            // Se comprueba si el código de respuesta de la API es 402
            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limitada")
            }
            // Se comprueba si la lista de resultados de la respuesta de la API está vacía
            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResult.Error("Recetas no encontradas")
            }
            // Si la respuesta de la API es exitosa, se devuelve un valor de éxito junto con los datos de la receta
            response.isSuccessful -> {
                val foodRecipes = response.body()
                return NetworkResult.Success(foodRecipes!!)
            }
            // Si la respuesta de la API contiene un error, se devuelve un valor de error junto con un mensaje de error
            else -> {
                return NetworkResult.Error(response.message().toString())
            }
        }
    }

    /**
     * Maneja la respuesta de la API de bromas de alimentos y la convierte en un objeto NetworkResult.
     *
     * @param response La respuesta de la API de bromas de alimentos.
     * @return Un objeto NetworkResult que contiene el resultado de la respuesta.
     */
    private fun handleFoodJokeResponse(response: Response<FoodJoke>): NetworkResult<FoodJoke>? {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                NetworkResult.Error("API Key Limited.")
            }
            response.isSuccessful -> {
                val foodJoke = response.body()
                NetworkResult.Success(foodJoke!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    /**
     * Verifica si el dispositivo tiene conexión a internet.
     *
     * @return true si hay conexión a internet, false de lo contrario.
     */
    private fun hasInternetConnection(): Boolean {
        // Se obtiene el servicio de conectividad del sistema del dispositivo a través de la aplicación
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        // Se obtiene la red activa actual
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        // Se obtienen las capacidades de la red activa
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        // Se verifica si la red activa tiene transporte de wifi, telefono o ethernet
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> return false
        }
    }
}





