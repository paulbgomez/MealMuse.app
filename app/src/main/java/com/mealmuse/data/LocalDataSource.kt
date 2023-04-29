package com.mealmuse.data

import com.mealmuse.data.database.RecipesDao
import com.mealmuse.data.database.RecipesEntity
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
    fun readDatabase(): Flow<List<RecipesEntity>> {
        return recipesDao.readRecipes()
    }

    //utiliza el objeto RecipesDao para insertar una entidad de recetas (RecipesEntity) en la tabla de
    // recetas (recipes_table) de la base de datos. Esta función es suspendida, lo que significa que puede ser
    // utilizada en corutinas y ejecutarse de manera asíncrona.
    suspend fun insertRecipes(recipesEntity: RecipesEntity) {
        recipesDao.insertRecipes(recipesEntity)
    }

}