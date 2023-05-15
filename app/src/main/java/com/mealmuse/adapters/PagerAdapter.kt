package com.mealmuse.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Adaptador de paginación para administrar los fragmentos en un ViewPager.
 */

/**
 * Crea un nuevo PagerAdapter.
 *
 * @param resultBundle    El paquete de resultados a pasar a los fragmentos.
 * @param fragments       La lista de fragmentos a mostrar en el ViewPager.
 * @param fragmentActivity La actividad del fragmento.
 */
class PagerAdapter(
    private val resultBundle: Bundle,
    private val fragments: ArrayList<Fragment>,
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    /**
     * Devuelve la cantidad de fragmentos en el adaptador.
     *
     * @return La cantidad de fragmentos.
     */
    @Override
    override fun getItemCount(): Int {
        return fragments.size
    }

    /**
     * Crea un nuevo fragmento en la posición especificada.
     *
     * @param position La posición del fragmento en el adaptador.
     * @return El fragmento creado.
     */
    @Override
    override fun createFragment(position: Int): Fragment {
        fragments[position].arguments = resultBundle
        return fragments[position]
    }
}
