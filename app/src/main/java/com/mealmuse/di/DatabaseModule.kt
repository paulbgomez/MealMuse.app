package com.mealmuse.di

import android.content.Context
import androidx.room.Room
import com.mealmuse.data.database.RecipesDatabase
import com.mealmuse.util.Constants.Companion.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * El módulo DatabaseModule proporciona las dependencias necesarias para la creación y acceso a la base de datos de recetas.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Proporciona una instancia de la base de datos de recetas.
     */
    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        RecipesDatabase::class.java,
        DATABASE_NAME
    ).build()

    /**
     * Proporciona una instancia del DAO (Data Access Object) de recetas.
     */
    @Singleton
    @Provides
    fun provideDao(database: RecipesDatabase) = database.recipesDao()

}