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
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// Tiene un constructor con una dependencia de Context y está anotada con @Inject.
// La anotación @ApplicationContext se utiliza para indicar que se debe proporcionar el
// contexto de la aplicación en lugar del contexto de la actividad o fragmento actual.

private val Context.dataStore by preferencesDataStore(PREFERENCES_NAME)

@ActivityRetainedScoped
class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {


    //define un objeto llamado PreferenceKeys que contiene cuatro propiedades estáticas.
    // Cada una de estas propiedades es un objeto de tipo Preferences.Key que se
    // utiliza para representar una clave de preferencia en el DataStore.
    private object PreferenceKeys {
        val selectedMealType = stringPreferencesKey(PREFERENCES_MEAL_TYPE)
        val selectedMealTypeId = intPreferencesKey(PREFERENCES_MEAL_TYPE_ID)
        val selectedDietType = stringPreferencesKey(PREFERENCES_DIET_TYPE)
        val selectedDietTypeId = intPreferencesKey(PREFERENCES_DIET_TYPE_ID)
        val backOnline = booleanPreferencesKey(PREFERENCES_BACK_ONLINE)
    }

    //Creamos una instancia de la clase DataStore utilizando una extensión de la clase Context llamada dataStore.

    private val dataStore: DataStore<Preferences> = context.dataStore

    // función suspendida que se utiliza para guardar los tipos de comida y
    // dieta seleccionados por el usuario en el DataStore de la aplicación.
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

    //la función saveBackOnline() se utiliza para guardar el estado de disponibilidad de red
    // de la aplicación (backOnline) en el almacenamiento de preferencias utilizando dataStore. Al utilizar la palabra clave suspend, se indica que esta función se puede llamar desde un contexto de suspensión, como una corutina.
    suspend fun saveBackOnline(backOnline: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.backOnline] = backOnline
        }
    }


    //Se utiliza para recuperar los tipos de comida y dieta seleccionados por el usuario del DataStore de la aplicación.
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

    //la variable readBackOnline es un Flow que se utiliza para leer el estado de disponibilidad de red guardado en dataStore. Si no se ha guardado ningún valor, se devuelve false.
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

//Define un modelo de datos para los tipos de comida y dieta seleccionados por el usuario en la aplicación.
data class MealAndDietType(
    val selectedMealType: String,
    val selectedMealTypeId: Int,
    val selectedDietType: String,
    val selectedDietTypeId: Int
)