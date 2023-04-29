package com.mealmuse.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipesDao {

    //Insertar los datos en la Entidad
    //Suspend lo usamos para más adelante utilizarlo en las coroutinas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipesEntity: RecipesEntity)

    //Para leer las recetas
    //Flow para fluir las recetas hacia abajo de la lista que hemos generado
    @Query("SELECT * FROM recipes_table ORDER BY id ASC")
    fun readRecipes(): Flow<List<RecipesEntity>>

}