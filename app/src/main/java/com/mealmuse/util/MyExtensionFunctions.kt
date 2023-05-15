package com.mealmuse.util

import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * Extensión para observar un LiveData una sola vez.
 *
 * @param lifecycleOwner El LifecycleOwner al que se asociará el Observer.
 * @param observer El Observer que se ejecutará una vez cuando cambie el valor del LiveData.
 */
fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            removeObserver(this)
            observer.onChanged(value)
        }
    })
}

/**
 * Función de extensión para recuperar un objeto Parcelable de un Bundle.
 *
 * @param key La clave del objeto Parcelable en el Bundle.
 * @return El objeto Parcelable recuperado, o null si no se encuentra.
 */
inline fun <reified T : Parcelable> Bundle.retrieveParcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}