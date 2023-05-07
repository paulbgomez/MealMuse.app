package com.mealmuse.data

import com.mealmuse.data.database.RecipesDao
import com.mealmuse.data.database.entities.FavoritesEntity
import com.mealmuse.data.database.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

//Esta clase utiliza la anotación @Inject para indicar que se debe proporcionar una
// instancia de RecipesDao a través de la inyección de dependencias.
class LocalDataSource @Inject constructor(
    private val recipesDao: RecipesDao
) {

    //utiliza el objeto RecipesDao para leer los datos de la tabla de recetas (recipes_table) de la base de datos.
    // Retorna un objeto Flow que fluirá una lista de entidades
    // de recetas (RecipesEntity) cada vez que la base de datos sea actualizada.
    fun readRecipes(): Flow<List<RecipesEntity>> {
        return recipesDao.readRecipes()
    }

    //utiliza el objeto RecipesDao para insertar una entidad de recetas (RecipesEntity) en la tabla de
    // recetas (recipes_table) de la base de datos. Esta función es suspendida, lo que significa que puede ser
    // utilizada en corutinas y ejecutarse de manera asíncrona.
    suspend fun insertRecipes(recipesEntity: RecipesEntity) {
        recipesDao.insertRecipes(recipesEntity)
    }

    // utiliza el objeto RecipesDao para leer los datos de la tabla de recetas favoritas (favorite_recipes_table) de la base de datos.
    // Devuelve un objeto Flow que fluirá una lista de entidades de recetas favoritas (FavoritesEntity) cada vez que la base de datos sea actualizada.
    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>> {
        return recipesDao.readFavoriteRecipes()
    }

    //utiliza el objeto RecipesDao para insertar una entidad de recetas favoritas (FavoritesEntity) en la tabla de recetas favoritas (favorite_recipes_table) de la base de datos. Esta función es suspendida,
    // lo que significa que puede ser utilizada en corutinas y ejecutarse de manera asíncrona.
    suspend fun insertFavoriteRecipes(favoritesEntity: FavoritesEntity) {
        recipesDao.insertFavoriteRecipe(favoritesEntity)
    }

    //utiliza el objeto RecipesDao para eliminar una entidad de recetas favoritas (FavoritesEntity) de la tabla de recetas favoritas (favorite_recipes_table) de la base de datos. Esta función es suspendida,
    // lo que significa que puede ser utilizada en corutinas y ejecutarse de manera asíncrona.
    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) {
        recipesDao.deleteFavoriteRecipe(favoritesEntity)
    }

    //utiliza el objeto RecipesDao para eliminar todas las entidades de recetas favoritas (FavoritesEntity) de la tabla de recetas favoritas (favorite_recipes_table) de la base de datos. Esta función es suspendida,
    // lo que significa que puede ser utilizada en corutinas y ejecutarse de manera asíncrona.
    suspend fun deleteAllFavoriteRecipes() {
        recipesDao.deleteAllFavoriteRecipes()
    }

}