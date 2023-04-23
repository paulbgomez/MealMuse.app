package com.mealmuse.util

sealed class NetworkResult<T>(
    val data: T? = null, // Datos que se reciben de la llamada de red.
    val message: String? = null // Mensaje de error, si hay un error.
) {

    // Representa una llamada de red se realizo con éxito.
    class Success<T>(data: T): NetworkResult<T>(data)

    // Representa un error en la llamada de red.
    class Error<T>(message: String?, data: T? = null): NetworkResult<T>(data, message)

    // Representa el caso en que la llamada de red se esta realizando y aun no se ha completado.
    class Loading<T>: NetworkResult<T>()

}
