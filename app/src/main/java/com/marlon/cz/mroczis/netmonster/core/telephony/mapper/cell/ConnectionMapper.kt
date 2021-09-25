package com.marlon.cz.mroczis.netmonster.core.telephony.mapper.cell

import android.annotation.TargetApi
import android.os.Build
import android.telephony.CellInfo
import com.marlon.cz.mroczis.netmonster.core.model.connection.IConnection
import com.marlon.cz.mroczis.netmonster.core.model.connection.NoneConnection
import com.marlon.cz.mroczis.netmonster.core.model.connection.PrimaryConnection
import com.marlon.cz.mroczis.netmonster.core.model.connection.SecondaryConnection

/**
 * [CellInfo] -> [IConnection]
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
internal fun CellInfo.mapConnection(): IConnection =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        when(cellConnectionStatus) {
            CellInfo.CONNECTION_PRIMARY_SERVING ->
                PrimaryConnection()
            CellInfo.CONNECTION_SECONDARY_SERVING ->
                SecondaryConnection(isGuess = false)
            else ->
                // Xiaomi Mi A1 returns CellInfo.CONNECTION_NONE & isRegistered = true
                if (isRegistered) {
                    PrimaryConnection()
                } else {
                    NoneConnection()
                }
        }
    } else {
        if (isRegistered) {
            PrimaryConnection()
        } else {
            NoneConnection()
        }
    }