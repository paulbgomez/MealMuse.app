package com.mealmuse.data.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mealmuse.models.FoodJoke
import com.mealmuse.util.Constants.Companion.FOOD_JOKE_TABLE



//@Embedded significa que los campos de FoodJoke se almacenan en la misma tabla que la entidad FoodJokeEntity, en lugar de en una tabla separada.


/**
 * Entidad de chiste de comida.
 *
 * Esta clase representa una entidad de chiste de comida en la base de datos.
 * Contiene los campos necesarios y las anotaciones de Room para su uso en la persistencia de datos.
 *
 * @param foodJoke El objeto de chiste de comida.
 * @param id       El ID de la entidad.
 */
@Entity(tableName = FOOD_JOKE_TABLE)
class FoodJokeEntity(
    @Embedded
    var foodJoke: FoodJoke
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}