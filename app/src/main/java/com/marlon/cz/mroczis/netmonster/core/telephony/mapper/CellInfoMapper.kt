package com.marlon.cz.mroczis.netmonster.core.telephony.mapper

import android.Manifest
import android.annotation.TargetApi
import android.os.Build
import android.telephony.*
import androidx.annotation.RequiresPermission
import com.marlon.cz.mroczis.netmonster.core.model.cell.ICell
import com.marlon.cz.mroczis.netmonster.core.telephony.mapper.cell.mapCell
import com.marlon.cz.mroczis.netmonster.core.telephony.mapper.cell.mapConnection
import com.marlon.cz.mroczis.netmonster.core.telephony.mapper.cell.mapSignal

/**
 * Transforms result of [TelephonyManager.getAllCellInfo] into our list
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
class CellInfoMapper(
    private val subId: Int
) : ICellMapper<List<CellInfo>?> {

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    override fun map(model: List<CellInfo>?): List<ICell> =
        model?.mapNotNull {
            if (it is CellInfoGsm) {
                mapGsm(it)
            } else if (it is CellInfoLte) {
                mapLte(it)
            } else if (it is CellInfoCdma) {
                mapCdma(it)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && it is CellInfoWcdma) {
                mapWcdma(it)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && it is CellInfoTdscdma) {
                mapTdscdma(it)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && it is CellInfoNr) {
                mapNr(it)
            } else null
        } ?: emptyList()


    private fun mapGsm(model: CellInfoGsm): ICell? {
        val connection = model.mapConnection()
        val signal = model.cellSignalStrength.mapSignal()
        return model.cellIdentity.mapCell(subId, connection, signal)
    }

    private fun mapLte(model: CellInfoLte): ICell? {
        val connection = model.mapConnection()
        val signal =  model.cellSignalStrength.mapSignal()
        return model.cellIdentity.mapCell(subId, connection, signal)
    }

    private fun mapCdma(model: CellInfoCdma): ICell? {
        val connection = model.mapConnection()
        val signal = model.cellSignalStrength.mapSignal()
        return model.cellIdentity.mapCell(subId, connection, signal)
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun mapWcdma(model: CellInfoWcdma): ICell? {
        val connection = model.mapConnection()
        val signal = model.cellSignalStrength.mapSignal()
        return model.cellIdentity.mapCell(subId, connection, signal)
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private fun mapTdscdma(model: CellInfoTdscdma): ICell? {
        val connection = model.mapConnection()
        val signal = model.cellSignalStrength.mapSignal()
        return model.cellIdentity.mapCell(subId, connection, signal)
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private fun mapNr(model: CellInfoNr): ICell? {
        val connection = model.mapConnection()
        val signal = (model.cellSignalStrength as? CellSignalStrengthNr)?.mapSignal()
        return (model.cellIdentity as? CellIdentityNr)?.mapCell(subId, connection, signal)
    }

}