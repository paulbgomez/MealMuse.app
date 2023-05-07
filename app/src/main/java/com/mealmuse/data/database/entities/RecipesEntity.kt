package com.mealmuse.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mealmuse.models.FoodRecipe
import com.mealmuse.util.Constants.Companion.RECIPES_TABLE

//Creamos la entidad en la BBDD dandole el nombre que tenemos puesto en la constante
//Como parámetro pasaremos la comida, la receta en este caso
@Entity(tableName = RECIPES_TABLE)
class RecipesEntity(var foodRecipe: FoodRecipe) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}