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

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    //proporciona una instancia única de la base de datos RecipesDatabase y el objeto RecipesDao que se utilizará
    // para realizar operaciones de lectura
    // y escritura en la tabla de recetas (recipes_table).
    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        RecipesDatabase::class.java,
        DATABASE_NAME
    ).build()

    //La función provideDao es una función de fábrica que proporciona una instancia de
    // RecipesDao utilizando la instancia de RecipesDatabase proporcionada como argumento.
    // La anotación @Singleton se utiliza para indicar que solo se debe proporcionar una única
    // instancia de este objeto en el ámbito de la aplicación.
    @Singleton
    @Provides
    fun provideDao(database: RecipesDatabase) = database.recipesDao()

}