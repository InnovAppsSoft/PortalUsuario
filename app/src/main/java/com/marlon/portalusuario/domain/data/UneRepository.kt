package com.marlon.portalusuario.domain.data

import com.marlon.portalusuario.une.Une
import kotlinx.coroutines.flow.Flow

interface UneRepository {
    val allUnes: Flow<List<Une>>

    suspend fun insertUne(une: Une)

    suspend fun updateUne(une: Une)

    suspend fun deleteUne(une: Une)

    suspend fun deleteAllUnes()
}
