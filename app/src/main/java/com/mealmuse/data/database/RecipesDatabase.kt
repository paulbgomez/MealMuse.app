// RecipesDatabase.kt
package com.mealmuse.data.database

import android.content.Context
import androidx.room.*
import com.mealmuse.data.database.entities.FavoritesEntity
import com.mealmuse.data.database.entities.FoodJokeEntity
import com.mealmuse.data.database.entities.RecipesEntity

/**
 * Base de datos de recetas.
 *
 * Esta clase abstracta representa la base de datos de recetas. Define las entidades que contiene,
 * la versión de la base de datos y las conversiones de tipo necesarias. También proporciona el acceso
 * al DAO de recetas a través del método abstracto recipesDao().
 */
@Database(
    entities = [RecipesEntity::class, FavoritesEntity::class, FoodJokeEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(RecipesTypeConverter::class)
abstract class RecipesDatabase: RoomDatabase() {

    /**
     * Obtiene el DAO de recetas.
     *
     * @return El objeto DAO de recetas.
     */
    abstract fun recipesDao(): RecipesDao

    companion object {
        /**
         * Nombre de la base de datos.
         */
        private const val DATABASE_NAME = "recipes_db"

        /**
         * Instancia de la base de datos.
         */
        private var instance: RecipesDatabase? = null


        /**
         * Obtiene una instancia de la base de datos.
         *
         * @param context El contexto de la aplicación.
         * @return La instancia de la base de datos.
         */
        fun getInstance(context: Context): RecipesDatabase {
            if (instance == null) {
                synchronized(RecipesDatabase::class) {
                    instance = buildDatabase(context)
                }
            }
            return instance!!
        }

        /**
         * Construye la base de datos.
         *
         * @param context El contexto de la aplicación.
         * @return La instancia de la base de datos construida.
         */
        private fun buildDatabase(context: Context): RecipesDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                RecipesDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
