package com.mealmuse.data


import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

/**
 * La clase Repository actúa como una capa intermedia entre las fuentes de datos remota y local.
 * Proporciona métodos para acceder y manipular los datos de recetas de alimentos tanto de la fuente de datos remota como de la fuente de datos local.
 */
@ViewModelScoped
class Repository @Inject constructor(
    remoteDataSource: RemoteDataSource,
    localDataSource: LocalDataSource
){
    /**
     * Fuente de datos remota para acceder a los datos de recetas de alimentos.
     */
    val remote = remoteDataSource

    /**
     * Fuente de datos local para acceder a los datos de recetas de alimentos.
     */
    val local = localDataSource
}