package com.mealmuse.data

import com.mealmuse.data.database.RecipesDao
import com.mealmuse.data.database.entities.FavoritesEntity
import com.mealmuse.data.database.entities.FoodJokeEntity
import com.mealmuse.data.database.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Clase de origen de datos local que interactúa con la base de datos y proporciona operaciones de lectura y escritura.
 */
class LocalDataSource @Inject constructor(
    private val recipesDao: RecipesDao
) {

    /**
     * Lee todas las recetas desde la base de datos.
     */
    fun readRecipes(): Flow<List<RecipesEntity>> {
        return recipesDao.readRecipes()
    }

    /**
     * Inserta una receta en la base de datos.
     */
    suspend fun insertRecipes(recipesEntity: RecipesEntity) {
        recipesDao.insertRecipes(recipesEntity)
    }

    /**
     * Lee todas las recetas favoritas desde la base de datos.
     */
    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>> {
        return recipesDao.readFavoriteRecipes()
    }

    /**
     * Lee todos los chistes de comida desde la base de datos.
     */
    fun readFoodJoke(): Flow<List<FoodJokeEntity>> {
        return recipesDao.readFoodJoke()
    }

    /**
     * Inserta una receta favorita en la base de datos.
     */
    suspend fun insertFavoriteRecipes(favoritesEntity: FavoritesEntity) {
        recipesDao.insertFavoriteRecipe(favoritesEntity)
    }

    /**
     * Inserta un chiste de comida en la base de datos.
     */
    suspend fun insertFoodJoke(foodJokeEntity: FoodJokeEntity) {
        recipesDao.insertFoodJoke(foodJokeEntity)
    }

    /**
     * Elimina una receta favorita de la base de datos.
     */
    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) {
        recipesDao.deleteFavoriteRecipe(favoritesEntity)
    }

    /**
     * Elimina todas las recetas favoritas de la base de datos.
     */
    suspend fun deleteAllFavoriteRecipes() {
        recipesDao.deleteAllFavoriteRecipes()
    }

}