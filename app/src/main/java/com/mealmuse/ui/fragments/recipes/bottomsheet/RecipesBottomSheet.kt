package com.mealmuse.ui.fragments.recipes.bottomsheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.mealmuse.R
import com.mealmuse.databinding.RecipesBottomSheetBinding
import com.mealmuse.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.mealmuse.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.mealmuse.viewmodels.RecipesViewModel
import java.util.*

/**
 * Fragmento del Bottom Sheet que muestra opciones de filtro para las recetas.
 */
class RecipesBottomSheet : BottomSheetDialogFragment() {

    private lateinit var recipesViewModel: RecipesViewModel

    private var _binding: RecipesBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var mealTypeChip = DEFAULT_MEAL_TYPE
    private var mealTypeChipId = 0
    private var dietTypeChip = DEFAULT_DIET_TYPE
    private var dietTypeChipId = 0

    /**
     * Crea el fragmento y el ViewModel asociado.
     *
     * @param savedInstanceState El estado previamente guardado del fragmento, si existe.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Una vez creada la instancia del ViewModel, se puede acceder a ella en el resto del código de la Fragment a través de la variable recipesViewModel.
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    /**
     * Infla el diseño de este fragmento y configura los listeners de los chips de selección.
     *
     * @param inflater El inflater utilizado para inflar el diseño del fragmento.
     * @param container El contenedor padre en el cual el diseño del fragmento será colocado.
     * @param savedInstanceState El estado previamente guardado del fragmento, si existe.
     * @return La vista inflada del fragmento.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = RecipesBottomSheetBinding.inflate(inflater, container, false)
        // establece los valores de los Chips en la vista de acuerdo con los valores almacenados en el almacenamiento de datos de la aplicación a través del ViewModel.
        recipesViewModel.readMealAndDietType.asLiveData().observe(viewLifecycleOwner) { value ->
            mealTypeChip = value.selectedMealType
            dietTypeChip = value.selectedDietType
            updateChip(value.selectedMealTypeId, binding.mealTypeChipGroup)
            updateChip(value.selectedDietTypeId, binding.dietTypeChipGroup)
    }

        //el listener establecido espera dos parámetros: el group que representa el ChipGroup al que se ha añadido el listener, y el selectedChipId que representa el ID del Chip que ha cambiado su estado.
        binding.mealTypeChipGroup.setOnCheckedStateChangeListener { group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId.first())
            //na vez encontrado, se obtiene el texto del Chip seleccionado y se convierte a minúsculas utilizando el método lowercase de la clase String.
            val selectedMealType = chip.text.toString().lowercase(Locale.ROOT)
            // el valor de selectedMealType se asigna a la variable mealTypeChip. También se asigna el valor de selectedChipId.first() a la variable mealTypeChipId.
            mealTypeChip = selectedMealType
            //es una colección de IDs de los chips seleccionados en el grupo, pero en este caso solo hay un chip seleccionado, por lo que se puede obtener su ID con el método first().
            mealTypeChipId = selectedChipId.first()
        }

        binding.dietTypeChipGroup.setOnCheckedStateChangeListener { group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId.first())
            val selectedDietType = chip.text.toString().lowercase(Locale.ROOT)
            dietTypeChip = selectedDietType
            dietTypeChipId = selectedChipId.first()
        }

        binding.applyBtn.setOnClickListener {
            recipesViewModel.saveMealAndDietTypeTemp(
                mealTypeChip,
                mealTypeChipId,
                dietTypeChip,
                dietTypeChipId
            )
            //establece una acción de navegación desde el RecipesBottomSheet hasta el RecipesFragment utilizando el componente NavController de la biblioteca de navegación de Android.
            val action =
                RecipesBottomSheetDirections.actionRecipesBottomSheetToRecipesFragment(true)
            findNavController().navigate(action)
        }

        return binding.root
    }

    /**
     * Actualiza el estado de un chip en un ChipGroup según su ID.
     *
     * @param chipId El ID del chip que se actualizará.
     * @param chipGroup El ChipGroup que contiene el chip.
     */
    private fun updateChip(chipId: Int, chipGroup: ChipGroup) {
        if (chipId != 0) {
            try {
                val targetView = chipGroup.findViewById<Chip>(chipId)
                targetView.isChecked = true
                chipGroup.requestChildFocus(targetView, targetView)
            } catch (e: Exception) {
                Log.d("RecipesBottomSheet", e.message.toString())
            }
        }
    }

    /**
     * Limpia el binding cuando el fragmento es destruido.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}