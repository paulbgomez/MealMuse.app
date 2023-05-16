package com.mealmuse.util

/**
 * Clase sellada NetworkResult que representa el resultado de una llamada de red.
 * Tiene tres posibles estados: Success (éxito), Error (error) y Loading (cargando).
 *
 * @param T El tipo de datos que se reciben de la llamada de red.
 * @param data Los datos recibidos de la llamada de red.
 * @param message El mensaje de error, si hay un error.
 */
sealed class NetworkResult<T>(
    val data: T? = null, // Datos que se reciben de la llamada de red.
    val message: String? = null // Mensaje de error, si hay un error.
) {

    /**
     * Representa una llamada de red que se realizó con éxito.
     *
     * @param data Los datos recibidos de la llamada de red.
     */
    class Success<T>(data: T): NetworkResult<T>(data)

    /**
     * Representa un error en la llamada de red.
     *
     * @param message El mensaje de error.
     * @param data Los datos recibidos de la llamada de red, si los hay.
     */
    class Error<T>(message: String?, data: T? = null): NetworkResult<T>(data, message)

    /**
     * Representa el caso en el que la llamada de red se está realizando y aún no se ha completado.
     */
    class Loading<T>: NetworkResult<T>()

}
