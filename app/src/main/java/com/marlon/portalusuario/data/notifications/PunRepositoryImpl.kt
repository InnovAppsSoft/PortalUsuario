package com.marlon.portalusuario.data.notifications

import android.content.Context
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.marlon.portalusuario.data.notifications.PunRepositoryImpl.Companion.NOTIFICATIONS_COUNT_KEY
import com.marlon.portalusuario.database.notifications.PunDAO
import com.marlon.portalusuario.domain.data.PunRepository
import com.marlon.portalusuario.punotifications.PUNotification
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PunRepositoryImpl
    @Inject
    constructor(
        private val punDao: PunDAO,
        @ApplicationContext private val appContext: Context,
    ) : PunRepository {
        override val allPUN: Flow<List<PUNotification>> = punDao.getAllPUNotification()

        @Suppress("TooGenericExceptionCaught")
        override suspend fun insertPUNotification(pun: PUNotification) {
            punDao.insertPUNotification(pun)
            val sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(appContext)
            val count = sharedPreferences.getInt(NOTIFICATIONS_COUNT_KEY, 0)
            sharedPreferences.edit().putInt(NOTIFICATIONS_COUNT_KEY, count + 1).apply()
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
            punDao.updatePUNotification(pun)
        }

        override suspend fun deletePUNotification(pun: PUNotification) {
            punDao.deletePUNotification(pun)
        }

        override suspend fun deleteAllPUNotifications() {
            punDao.deleteAllPUNotification()
        }

        companion object {
            const val NOTIFICATIONS_COUNT_KEY = "notifications_count"
        }
    }
