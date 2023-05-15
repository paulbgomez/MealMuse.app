package com.mealmuse.util

import androidx.recyclerview.widget.DiffUtil

/**
 * Clase RecipesDiffUtil que implementa la interfaz DiffUtil.Callback para calcular las diferencias
 * entre dos listas de elementos.
 *
 * @param T El tipo de elementos en las listas.
 * @param oldList La lista antigua de elementos.
 * @param newList La lista nueva de elementos.
 */
class RecipesDiffUtil<T>(
    private val oldList: List<T>, // La lista antigua de elementos
    private val newList: List<T> // La lista nueva de elementos
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size // Devuelve el tamaño de la lista antigua
    }

    override fun getNewListSize(): Int {
        return newList.size // Devuelve el tamaño de la lista nueva
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Devuelve true si los elementos en las posiciones dadas son iguales
        // (comparando por referencia)
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Devuelve true si los elementos en las posiciones dadas son iguales
        // (comparando por contenido)
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
