package com.mealmuse.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mealmuse.R
import com.mealmuse.adapters.PagerAdapter
import com.mealmuse.ui.fragments.ingredients.IngredientsFragment
import com.mealmuse.ui.fragments.instructions.InstructionsFragment
import com.mealmuse.ui.fragments.overview.OverviewFragment
class DetailsActivity : AppCompatActivity() {

    //Se obtienen los argumentos enviados desde el fragment anterior
    private val args by navArgs<DetailsActivityArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        //Se agrega una barra de herramientas en la actividad
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        //Se habilita el botón de retroceso en la barra de herramientas
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Se crea una lista de fragmentos para mostrar en la vista del ViewPager2
        val fragments = ArrayList<Fragment>()
        fragments.add(OverviewFragment())
        fragments.add(IngredientsFragment())
        fragments.add(InstructionsFragment())

        //Se agregan los títulos correspondientes a cada fragmento
        val titles = ArrayList<String>()
        titles.add("Overview")
        titles.add("Ingredients")
        titles.add("Instructions")

        //Se crea un Bundle para pasar los datos de la receta a los fragmentos
        val resultBundle = Bundle()
        resultBundle.putParcelable("recipeBundle", args.result)

        //Se crea un PagerAdapter para manejar los fragmentos en el ViewPager2
        val adapter = PagerAdapter(
            resultBundle,
            fragments,
            titles,
            supportFragmentManager,
            lifecycle
        )

        //Se configura el ViewPager2 con el adaptador creado
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = adapter

        //Se configura el TabLayout para mostrar los títulos de los fragmentos
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    //Se maneja el evento de hacer clic en el botón de retroceso
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
