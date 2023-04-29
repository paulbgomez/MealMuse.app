package com.mealmuse.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

//Ponemos una serie de requisitos
// Aumentar el número de versión
// La BBDD que vamos a querer
//Esto lo hacemos para tener un historial de nuestra BBDD
@Database(
    entities = [RecipesEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RecipesTypeConverter::class)
abstract class RecipesDatabase: RoomDatabase() {

    abstract fun recipesDao(): RecipesDao

}