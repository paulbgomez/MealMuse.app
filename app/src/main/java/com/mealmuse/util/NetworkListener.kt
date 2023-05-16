package com.mealmuse.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Clase NetworkListener que implementa ConnectivityManager.NetworkCallback().
 * Esta clase se utiliza para verificar la disponibilidad de la red y notificar a los observadores de los cambios en la conectividad de red.
 */
@ExperimentalCoroutinesApi
class NetworkListener : ConnectivityManager.NetworkCallback() {

    //MutableStateFlow es una clase de Kotlin que proporciona una forma de representar un valor que puede cambiar con el tiempo y notificar a los observadores de los cambios.
    private val isNetworkAvailable = MutableStateFlow(false)

    /**
     * Verifica la disponibilidad de la red y devuelve un MutableStateFlow<Boolean> que representa el estado actual de la conexión de red.
     *
     * @param context El contexto utilizado para obtener el servicio ConnectivityManager.
     * @return El MutableStateFlow<Boolean> que representa el estado actual de la conexión de red.
     */
    fun checkNetworkAvailability(context: Context): MutableStateFlow<Boolean> {
        val connectivityManager =
            //La función toma un objeto Context como argumento y utiliza el servicio ConnectivityManager para obtener información sobre el estado de la red.
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //se registra un NetworkCallback (un tipo de devolución de llamada proporcionado por ConnectivityManager) para ser notificado de los cambios en la conectividad de red.
        connectivityManager.registerDefaultNetworkCallback(this)

        //se comprueba si hay una red activa y si no hay una red activa, se establece el valor de isNetworkAvailable en false y se devuelve isNetworkAvailable.
        val network =
            connectivityManager.activeNetwork
        if (network == null) {
            isNetworkAvailable.value = false
            return isNetworkAvailable
        }

        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        if (networkCapabilities == null) {
            isNetworkAvailable.value = false
            return isNetworkAvailable
        }

        // la función devuelve el valor de la variable isNetworkAvailable después de establecer su valor en función de si la conexión de red actual es Wi-Fi, celular o no está disponible.
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                isNetworkAvailable.value = true
                isNetworkAvailable
            }
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                isNetworkAvailable.value = true
                isNetworkAvailable
            }
            else -> {
                isNetworkAvailable.value = false
                isNetworkAvailable
            }
        }
    }

    /**
     * Se llama cuando una red está disponible y actualiza el estado de la isNetworkAvailable variable a true.
     *
     * @param network La red disponible.
     */
    override fun onAvailable(network: Network) {
        isNetworkAvailable.value = true
    }

    /**
     * Se llama cuando se pierde una red y actualiza el estado de la isNetworkAvailable variable a false.
     *
     * @param network La red perdida.
     */
    override fun onLost(network: Network) {
        isNetworkAvailable.value = false
    }
}