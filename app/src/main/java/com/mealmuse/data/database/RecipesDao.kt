package com.mealmuse.data.database

import androidx.room.*
import com.mealmuse.data.database.entities.FavoritesEntity
import com.mealmuse.data.database.entities.FoodJokeEntity
import com.mealmuse.data.database.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO de recetas.
 *
 * Esta interfaz define los métodos de acceso a la base de datos para las recetas.
 * Contiene métodos para insertar, leer y eliminar recetas y recetas favoritas.
 */
@Dao
interface RecipesDao {

    /**
     * Inserta los datos en la entidad RecipesEntity.
     *
     * @param recipesEntity La entidad de recetas a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipesEntity: RecipesEntity)

    /**
     * Lee las recetas desde la tabla "recipes_table" ordenadas por "id" de forma ascendente.
     *
     * @return Un objeto Flow que emite una lista de entidades de recetas.
     */
    @Query("SELECT * FROM recipes_table ORDER BY id ASC")
    fun readRecipes(): Flow<List<RecipesEntity>>

    /**
     * Inserta una broma de comida en la entidad FoodJokeEntity.
     *
     * @param foodJokeEntity La entidad de broma de comida a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodJoke(foodJokeEntity: FoodJokeEntity)

    /**
     * Inserta una receta favorita en la entidad FavoritesEntity.
     *
     * @param favoritesEntity La entidad de receta favorita a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity)

    /**
     * Lee todas las recetas favoritas desde la tabla "favorite_recipes_table" ordenadas por "id" de forma ascendente.
     *
     * @return Un objeto Flow que emite una lista de entidades de recetas favoritas.
     */
    @Query("SELECT * FROM favorite_recipes_table ORDER BY id ASC")
    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>>

    /**
     * Lee todas las bromas de comida desde la tabla "food_joke_table" ordenadas por "id" de forma ascendente.
     *
     * @return Un objeto Flow que emite una lista de entidades de bromas de comida.
     */
    @Query("SELECT * FROM food_joke_table ORDER BY id ASC")
    fun readFoodJoke(): Flow<List<FoodJokeEntity>>

    /**
     * Elimina una receta favorita de la entidad FavoritesEntity.
     *
     * @param favoritesEntity La entidad de receta favorita a eliminar.
     */
    @Delete
    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity)

    /**
     * Elimina todas las recetas favoritas de la tabla "favorite_recipes_table".
     */
    @Query("DELETE FROM favorite_recipes_table")
    suspend fun deleteAllFavoriteRecipes()

}