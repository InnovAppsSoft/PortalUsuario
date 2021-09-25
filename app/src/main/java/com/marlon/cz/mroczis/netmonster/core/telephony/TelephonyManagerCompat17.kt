package com.marlon.cz.mroczis.netmonster.core.telephony

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import androidx.annotation.WorkerThread
import com.marlon.cz.mroczis.netmonster.core.callback.CellCallbackError
import com.marlon.cz.mroczis.netmonster.core.callback.CellCallbackSuccess

/**
 * Modifies some functionalities of [TelephonyManager] and unifies access to
 * methods across all platform versions.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
internal open class TelephonyManagerCompat17(
    context: Context,
    subId: Int = Integer.MAX_VALUE
) : TelephonyManagerCompat14(context, subId){


    @WorkerThread
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun getAllCellInfo(
            onSuccess: CellCallbackSuccess,
            onError: CellCallbackError?
    ) {
        onSuccess.invoke(cellInfoMapper.map(telephony.allCellInfo))
    }


}