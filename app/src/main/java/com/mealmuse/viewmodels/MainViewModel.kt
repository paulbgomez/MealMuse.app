package com.mealmuse.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.*
import com.mealmuse.data.Repository
import com.mealmuse.data.database.entities.FavoritesEntity
import com.mealmuse.data.database.entities.RecipesEntity
import com.mealmuse.models.FoodRecipe
import com.mealmuse.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

// Se utiliza la anotación @HiltViewModel para indicar que esta clase es un ViewModel que utiliza Hilt para la inyección de dependencias
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
): AndroidViewModel(application) {

    /** ROOM DATABASE**/

    //Define un objeto LiveData que fluirá una lista de entidades de recetas obtenidas de la base de datos.
    // Utiliza la función readDatabase() de la clase LocalDataSource para acceder a los datos locales.
    // La función asLiveData() se utiliza para convertir el objeto Flow retornado por readDatabase() a un objeto LiveData.
    val readRecipes: LiveData<List<RecipesEntity>> = repository.local.readRecipes().asLiveData()
    val readFavoriteRecipes: LiveData<List<FavoritesEntity>> = repository.local.readFavoriteRecipes().asLiveData()

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

    // La función "getRecipes" se utiliza para iniciar una llamada a la API y obtener los datos de la receta
    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queries) // Se llama a la función "getRecipesSafeCall" para realizar la llamada segura a la API
    }

    fun searchRecipes(searchQuery: Map<String, String>) = viewModelScope.launch {
        searchRecipesSafeCall(searchQuery)
    }

    // La función "getRecipesSafeCall" se utiliza para realizar una llamada segura a la API y procesar la respuesta
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

    //este código asegura que se realice una búsqueda segura de recetas de alimentos, maneja los diferentes escenarios de resultados de la API y actualiza searchedRecipesResponse con información adicional de si la búsqueda tuvo éxito o no, errores que puedan haber ocurrido y el estado actual de la búsqueda.
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

    //Es una función privada que se utiliza para almacenar en caché una receta (FoodRecipe) en la base de datos local.
    //Actúa como una capa intermedia entre la capa de presentación de la aplicación y la capa de datos, permitiendo que la aplicación almacene datos en caché para un acceso rápido y sin conexión.
    private fun offlineCacheRecipes(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipesEntity(foodRecipe)
        insertRecipes(recipesEntity)
    }

    // La función "handleFoodRecipesResponse" se utiliza para procesar la respuesta de la API y devolver un valor de éxito o error
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

    // Función que verifica si hay conexión a internet
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





