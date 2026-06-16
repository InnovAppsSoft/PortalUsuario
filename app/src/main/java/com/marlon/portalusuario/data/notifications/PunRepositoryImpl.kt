package com.marlon.portalusuario.data.notifications

import android.content.Context
import android.widget.Toast
import com.marlon.portalusuario.data.ServicesDao
import com.marlon.portalusuario.data.notifications.PunRepositoryImpl.Companion.NOTIFICATIONS_COUNT_KEY
import com.marlon.portalusuario.data.preferences.notificationsCountFlow
import com.marlon.portalusuario.data.preferences.setNotificationsCount
import com.marlon.portalusuario.domain.data.PunRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.marlon.portalusuario.punotifications.PUNotification
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PunRepositoryImpl
    @Inject
    constructor(
        private val dao: ServicesDao,
        @ApplicationContext private val appContext: Context,
    ) : PunRepository {
        override val allPUN: Flow<List<PUNotification>> = dao.getAllPUNotifications()

        @Suppress("TooGenericExceptionCaught")
        override suspend fun insertPUNotification(pun: PUNotification) {
            dao.insertPUNotification(pun)
            val count = runBlocking { appContext.notificationsCountFlow().first() }
            runBlocking { appContext.setNotificationsCount(count + 1) }
            try {
                Toast
                    .makeText(
                        appContext,
                        "Nuevo mensaje de Portal Usuario",
                        Toast.LENGTH_LONG,
                    ).show()
            } catch (ex: RuntimeException) {
                ex.printStackTrace()
            }
        }

        override suspend fun updatePUNotification(pun: PUNotification) {
            dao.updatePUNotification(pun)
        }

        override suspend fun deletePUNotification(pun: PUNotification) {
            dao.deletePUNotification(pun)
        }

        override suspend fun deleteAllPUNotifications() {
            dao.deleteAllPUNotifications()
        }

        companion object {
            const val NOTIFICATIONS_COUNT_KEY = "notifications_count"
        }
    }
