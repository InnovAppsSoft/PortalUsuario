package com.marlon.portalusuario.database.notifications

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import com.marlon.portalusuario.PUNotifications.PUNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PunRepository(application: Application) {
    private val punDao: PunDAO
    private val database: PunDataBase
    val allPUN: LiveData<List<PUNotification>>
    private val app: Application = application

    init {
        database = PunDataBase.getInstance(application)
        punDao = database.dao()
        allPUN = punDao.getAllPUNotification()
    }

    @Suppress("TooGenericExceptionCaught")
    fun insertPUNotification(pun: PUNotification) {
        CoroutineScope(Dispatchers.IO).launch {
            punDao.insertPUNotification(pun)
            val sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(app.applicationContext)
            val count = sharedPreferences.getInt("notifications_count", 0)
            sharedPreferences.edit().putInt("notifications_count", count + 1).apply()
            try {
                Toast
                    .makeText(
                        app.applicationContext,
                        "Nuevo mensaje de Portal Usuario",
                        Toast.LENGTH_LONG,
                    ).show()
            } catch (ex: RuntimeException) {
                ex.printStackTrace()
            }
        }
    }

    fun updatePUNotification(pun: PUNotification) {
        CoroutineScope(Dispatchers.IO).launch {
            punDao.updatePUNotification(pun)
        }
    }

    fun deletePUNotification(pun: PUNotification) {
        CoroutineScope(Dispatchers.IO).launch {
            punDao.deletePUNotification(pun)
        }
    }

    fun deleteAllPUNotifications() {
        CoroutineScope(Dispatchers.IO).launch {
            punDao.deleteAllPUNotification()
        }
    }
}
