package com.mealmuse.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mealmuse.models.FoodRecipe
import com.mealmuse.util.Constants.Companion.RECIPES_TABLE

/**
 * Entidad de recetas.
 *
 * Esta clase representa una entidad de recetas en la base de datos.
 * Contiene los campos necesarios y las anotaciones de Room para su uso en la persistencia de datos.
 *
 * @param foodRecipe El objeto de receta de comida.
 * @param id         El ID de la entidad.
 */
@Entity(tableName = RECIPES_TABLE)
class RecipesEntity(var foodRecipe: FoodRecipe) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}