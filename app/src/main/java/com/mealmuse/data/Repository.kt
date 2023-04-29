package com.mealmuse.data

import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/**
 * Con esta anotacion nos aseguramos de que el Repository binding sigue funcionando
 * a pesar de que haya cambios en la configuracion o activity
 */
@ActivityRetainedScoped
class Repository @Inject constructor(
    remoteDataSource: RemoteDataSource,
    localDataSource: LocalDataSource
){
    val remote = remoteDataSource
    val local = localDataSource
}