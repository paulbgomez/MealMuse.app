package com.mealmuse.data.database

import androidx.room.*
import com.mealmuse.data.database.entities.FavoritesEntity
import com.mealmuse.data.database.entities.FoodJokeEntity
import com.mealmuse.data.database.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipesDao {

    // Inserta los datos en la entidad RecipesEntity
    // Utiliza "suspend" para que sea utilizado más adelante en las coroutines
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipesEntity: RecipesEntity)

    // Lee las recetas desde la tabla "recipes_table" ordenadas por "id" de forma ascendente
    // Utiliza "Flow" para mandar las recetas hacia abajo de la lista generada
    @Query("SELECT * FROM recipes_table ORDER BY id ASC")
    fun readRecipes(): Flow<List<RecipesEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodJoke(foodJokeEntity: FoodJokeEntity)

    // Inserta una receta favorita en la entidad FavoritesEntity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity)

    // Lee todas las recetas favoritas desde la tabla "favorite_recipes_table" ordenadas por "id" de forma ascendente
    // Utiliza "Flow" para mandar las recetas favoritas hacia abajo de la lista generada
    @Query("SELECT * FROM favorite_recipes_table ORDER BY id ASC")
    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>>

    @Query("SELECT * FROM food_joke_table ORDER BY id ASC")
    fun readFoodJoke(): Flow<List<FoodJokeEntity>>

    // Elimina una receta favorita de la entidad FavoritesEntity
    @Delete
    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity)

    // Elimina todas las recetas favoritas de la tabla "favorite_recipes_table"
    @Query("DELETE FROM favorite_recipes_table")
    suspend fun deleteAllFavoriteRecipes()

}