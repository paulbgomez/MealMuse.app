package com.mealmuse.data.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mealmuse.models.FoodJoke
import com.mealmuse.util.Constants.Companion.FOOD_JOKE_TABLE

//Esta es una clase de entidad de base de datos de Room que representa una broma de comida y se utiliza para almacenarla en la base de datos.

//@Embedded significa que los campos de FoodJoke se almacenan en la misma tabla que la entidad FoodJokeEntity, en lugar de en una tabla separada.
@Entity(tableName = FOOD_JOKE_TABLE)
class FoodJokeEntity(
    @Embedded
    var foodJoke: FoodJoke
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}