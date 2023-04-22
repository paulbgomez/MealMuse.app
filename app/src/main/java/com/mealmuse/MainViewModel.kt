package com.mealmuse

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mealmuse.data.Repository
import com.mealmuse.models.FoodRecipe
import com.mealmuse.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

// Se utiliza la anotación @HiltViewModel para indicar que esta clase es un ViewModel que utiliza Hilt para la inyección de dependencias
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
): AndroidViewModel(application) {

    // Se declara una variable de LiveData llamada "recipesResponse" que contendrá los datos de la respuesta de la API
    var recipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()

    // La función "getRecipes" se utiliza para iniciar una llamada a la API y obtener los datos de la receta
    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queries) // Se llama a la función "getRecipesSafeCall" para realizar la llamada segura a la API
    }

    // La función "getRecipesSafeCall" se utiliza para realizar una llamada segura a la API y procesar la respuesta
    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        recipesResponse.value = NetworkResult.Loading()
        // Se comprueba si el dispositivo tiene conexión a internet utilizando la función "hasInternetConnection"
        if(hasInternetConnection()) {
            // Si hay conexión a internet, se realiza una solicitud GET a la API
            try {
                val response = repository.remote.getRecipes(queries) // Se llama a la función "getRecipes" de la clase Repository para obtener los datos de la receta
                // Se llama a la función "handleFoodRecipesResponse" para procesar la respuesta de la API y se establece el valor de "recipesResponse" con el resultado
                recipesResponse.value = handleFoodRecipesResponse(response)
            } catch (e: Exception) {
                // Si se produce un error durante la llamada a la API, se establece un valor de error en "recipesResponse"
                recipesResponse.value = NetworkResult.Error("Recetas no encontradas")
            }
        } else {
            // Si no hay conexión a internet, se establece un valor de error en "recipesResponse"
            recipesResponse.value = NetworkResult.Error("No hay conexión a internet")
        }
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