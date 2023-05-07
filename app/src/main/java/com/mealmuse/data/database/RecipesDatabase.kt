// RecipesDatabase.kt
package com.mealmuse.data.database

import android.content.Context
import androidx.room.*
import com.mealmuse.data.database.entities.FavoritesEntity
import com.mealmuse.data.database.entities.RecipesEntity

@Database(
    entities = [RecipesEntity::class, FavoritesEntity::class],
    version = 2,
    exportSchema = false,
)
@TypeConverters(RecipesTypeConverter::class)
abstract class RecipesDatabase: RoomDatabase() {

    abstract fun recipesDao(): RecipesDao

    companion object {
        private const val DATABASE_NAME = "recipes_db"

        private var instance: RecipesDatabase? = null

        fun getInstance(context: Context): RecipesDatabase {
            if (instance == null) {
                synchronized(RecipesDatabase::class) {
                    instance = buildDatabase(context)
                }
            }
            return instance!!
        }

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
