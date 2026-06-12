package com.marlon.portalusuario.database.une

import android.app.Application
import androidx.lifecycle.LiveData
import com.marlon.portalusuario.une.Une
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UneRepository(application: Application) {
    private val uneDao: UneDAO
    private val database: UneDataBase
    val allUnes: LiveData<List<Une>>

    init {
        database = UneDataBase.getInstance(application)
        uneDao = database.dao()
        allUnes = uneDao.getAllUne()
    }

    fun insertUne(une: Une) {
        CoroutineScope(Dispatchers.IO).launch {
            uneDao.insertUne(une)
        }
    }

    fun updateUne(une: Une) {
        CoroutineScope(Dispatchers.IO).launch {
            uneDao.updateUne(une)
        }
    }

    fun deleteUne(une: Une) {
        CoroutineScope(Dispatchers.IO).launch {
            uneDao.deleteUne(une)
        }
    }

    fun deleteAllUnes() {
        CoroutineScope(Dispatchers.IO).launch {
            uneDao.deleteAllUne()
        }
    }
}
