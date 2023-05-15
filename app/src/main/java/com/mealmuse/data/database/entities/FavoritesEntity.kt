package com.mealmuse.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mealmuse.util.Constants.Companion.FAVORITE_RECIPES_TABLE


/**
 * Entidad de favoritos.
 *
 * Esta clase representa una entidad de favoritos en la base de datos.
 * Contiene los campos necesarios y las anotaciones de Room para su uso en la persistencia de datos.
 *
 * @param id       El ID de la entidad (generado automáticamente).
 * @param result   El objeto de resultado de la receta.
 */
@Entity(tableName = FAVORITE_RECIPES_TABLE)
class FavoritesEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var result: com.mealmuse.models.Result
)