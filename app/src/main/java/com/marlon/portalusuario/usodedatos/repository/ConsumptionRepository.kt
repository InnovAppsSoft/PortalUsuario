package com.marlon.portalusuario.usodedatos.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.marlon.portalusuario.usodedatos.database.ConsumptionDAO
import com.marlon.portalusuario.usodedatos.database.DailyConsumption
import com.marlon.portalusuario.usodedatos.util.DateUtils

class ConsumptionRepository(val consumptionDAO: ConsumptionDAO){

    val allConsumptions : LiveData<List<DailyConsumption>> = consumptionDAO.getAll()

    val getDayUsage : LiveData<DailyConsumption> = consumptionDAO.getDayUsage(DateUtils.getDayID())

    @WorkerThread
    suspend fun insert(dailyConsumption : DailyConsumption){
        consumptionDAO.insert(dailyConsumption)
    }

    @WorkerThread
    suspend fun updateWifiUsage(dayID : String, wifi : Long){
        consumptionDAO.updateWifiUsage(dayID, wifi)
    }

    @WorkerThread
    suspend fun updateMobileUsage(dayID : String, mobile : Long){
        consumptionDAO.updateMobileUsage(dayID, mobile)
    }

    @WorkerThread
    fun getDayUsageInBackgroundThread(dayID: String) : DailyConsumption{
        return consumptionDAO.getDayUsageInBackgroundThread(dayID)
    }

}