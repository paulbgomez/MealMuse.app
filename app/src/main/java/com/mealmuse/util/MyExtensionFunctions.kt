package com.mealmuse.util

import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

// se utiliza para observar cambios en un objeto LiveData y ejecutar una acción solo una vez. Esto es útil cuando
// solo se necesita ejecutar una acción la primera vez que cambia el valor de LiveData, en lugar de cada vez que cambia.
fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            removeObserver(this)
            observer.onChanged(value)
        }
    })
}

inline fun <reified T : Parcelable> Bundle.retrieveParcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}