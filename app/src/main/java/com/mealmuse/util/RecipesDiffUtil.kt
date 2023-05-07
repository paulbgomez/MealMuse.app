package com.mealmuse.util

import androidx.recyclerview.widget.DiffUtil

// Clase para calcular la diferencia entre dos listas de elementos
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
        // Devuelve verdadero si los elementos en las posiciones dadas son iguales
        // (comparando por referencia)
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Devuelve verdadero si los elementos en las posiciones dadas son iguales
        // (comparando por contenido)
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
