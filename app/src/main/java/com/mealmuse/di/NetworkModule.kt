package com.mealmuse.di

import com.mealmuse.util.Constants.Companion.BASE_URL
import com.mealmuse.data.network.FoodRecipesApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Proporciona una instancia del cliente OkHttp que se utilizará para las solicitudes de red.
     *
     * @return Una instancia de OkHttpClient configurada con los tiempos de espera de lectura y conexión deseados.
     */
    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Proporciona una instancia de GsonConverterFactory utilizada para convertir las respuestas JSON a clases de datos Kotlin.
     *
     * @return Una instancia de GsonConverterFactory.
     */
    @Singleton
    @Provides
    fun provideConvertorFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    /**
     * Proporciona una instancia de Retrofit que se puede utilizar para hacer llamadas API mediante la interfaz FoodRecipesApi.
     * BASE_URL sera la URL de nuestra API de recetas
     *
     * @param okHttpClient Una instancia de OkHttpClient que se utilizará para las solicitudes de red.
     * @param gsonConverterFactory Una instancia de GsonConverterFactory que se utilizará para el análisis JSON.
     * @return Una instancia de Retrofit.
     */
    @Singleton
    @Provides
    fun provideRetrofitInstance(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    /**
     * Proporciona una instancia de la interfaz FoodRecipesApi que se puede utilizar para hacer llamadas API.
     *
     * @param retrofit Una instancia de Retrofit que ha sido configurada para hacer llamadas API a la API de recetas de comida.
     * @return Una instancia de FoodRecipesApi.
     */
    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): FoodRecipesApi {
        return retrofit.create(FoodRecipesApi::class.java)
    }
}