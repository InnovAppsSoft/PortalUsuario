package com.marlon.portalusuario.domain.data

import com.marlon.portalusuario.feature.notifications.PUNotification
import kotlinx.coroutines.flow.Flow

interface PunRepository {
    val allPUN: Flow<List<PUNotification>>

    suspend fun insertPUNotification(pun: PUNotification)

    suspend fun updatePUNotification(pun: PUNotification)

    suspend fun deletePUNotification(pun: PUNotification)

    suspend fun deleteAllPUNotifications()
}
