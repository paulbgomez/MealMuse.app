package com.mealmuse.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.mealmuse.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.mealmuse.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.mealmuse.util.Constants.Companion.PREFERENCES_BACK_ONLINE
import com.mealmuse.util.Constants.Companion.PREFERENCES_DIET_TYPE
import com.mealmuse.util.Constants.Companion.PREFERENCES_DIET_TYPE_ID
import com.mealmuse.util.Constants.Companion.PREFERENCES_MEAL_TYPE
import com.mealmuse.util.Constants.Companion.PREFERENCES_MEAL_TYPE_ID
import com.mealmuse.util.Constants.Companion.PREFERENCES_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject


private val Context.dataStore by preferencesDataStore(PREFERENCES_NAME)

/**
 * Repositorio para acceder y guardar datos en el DataStore de la aplicación.
 */
@ViewModelScoped
class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {


    /**
     * Claves de preferencia utilizadas en el DataStore.
     */
    private object PreferenceKeys {
        val selectedMealType = stringPreferencesKey(PREFERENCES_MEAL_TYPE)
        val selectedMealTypeId = intPreferencesKey(PREFERENCES_MEAL_TYPE_ID)
        val selectedDietType = stringPreferencesKey(PREFERENCES_DIET_TYPE)
        val selectedDietTypeId = intPreferencesKey(PREFERENCES_DIET_TYPE_ID)
        val backOnline = booleanPreferencesKey(PREFERENCES_BACK_ONLINE)
    }

    /**
     * Instancia del DataStore utilizado para acceder a las preferencias.
     */
    private val dataStore: DataStore<Preferences> = context.dataStore

    /**
     * Guarda los tipos de comida y dieta seleccionados por el usuario en el DataStore.
     */
    suspend fun saveMealAndDietType(
        mealType: String,
        mealTypeId: Int,
        dietType: String,
        dietTypeId: Int
    ) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.selectedMealType] = mealType
            preferences[PreferenceKeys.selectedMealTypeId] = mealTypeId
            preferences[PreferenceKeys.selectedDietType] = dietType
            preferences[PreferenceKeys.selectedDietTypeId] = dietTypeId
        }
    }

    /**
     * Guarda el estado de disponibilidad de red en el DataStore.
     */
    suspend fun saveBackOnline(backOnline: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.backOnline] = backOnline
        }
    }


    /**
     * Lee los tipos de comida y dieta seleccionados por el usuario del DataStore.
     */
    val readMealAndDietType: Flow<MealAndDietType> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                //se emite un objeto Preferences vacío. Si se produce cualquier otra excepción, se relanza la excepción para que se maneje en otro lugar.
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
            //se utiliza para transformar los valores de preferencia en un objeto MealAndDietType. Dentro del lambda que se proporciona a map, se recuperan los valores de preferencia utilizando las claves de preferencia definidas en el objeto PreferenceKeys.
        .map { preferences ->
            val selectedMealType = preferences[PreferenceKeys.selectedMealType] ?: DEFAULT_MEAL_TYPE
            val selectedMealTypeId = preferences[PreferenceKeys.selectedMealTypeId] ?: 0
            val selectedDietType = preferences[PreferenceKeys.selectedDietType] ?: DEFAULT_DIET_TYPE
            val selectedDietTypeId = preferences[PreferenceKeys.selectedDietTypeId] ?: 0
            //se crea un nuevo objeto MealAndDietType utilizando los valores recuperados de preferencia y se emite a través del flujo.
            MealAndDietType(
                selectedMealType,
                selectedMealTypeId,
                selectedDietType,
                selectedDietTypeId
            )
        }

    /**
     * Lee el estado de disponibilidad de red del DataStore.
     */
    val readBackOnline: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {preferences ->
            val backOnline = preferences[PreferenceKeys.backOnline] ?: false
            backOnline
        }

}

/**
 * Modelo de datos para los tipos de comida y dieta seleccionados por el usuario en la aplicación.
 */
data class MealAndDietType(
    val selectedMealType: String,
    val selectedMealTypeId: Int,
    val selectedDietType: String,
    val selectedDietTypeId: Int
)