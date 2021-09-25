package com.marlon.cz.mroczis.netmonster.core.feature.detect

import android.Manifest
import android.os.Build
import android.telephony.TelephonyDisplayInfo
import androidx.annotation.RequiresPermission
import com.marlon.cz.mroczis.netmonster.core.INetMonster
import com.marlon.cz.mroczis.netmonster.core.db.NetworkTypeTable
import com.marlon.cz.mroczis.netmonster.core.db.model.NetworkType
import com.marlon.cz.mroczis.netmonster.core.model.DisplayInfo
import com.marlon.cz.mroczis.netmonster.core.model.annotation.SinceSdk
import com.marlon.cz.mroczis.netmonster.core.telephony.ITelephonyManagerCompat


/**
 * Attempts to detect LTE Advanced / LTE Carrier aggregation and NR in NSA mode
 *
 * Based on [TelephonyDisplayInfo]'s contents added in Android R.
 */
class DetectorLteAdvancedNrDisplayInfo : INetworkDetector {

    @SinceSdk(Build.VERSION_CODES.R)
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE])
    override fun detect(netmonster: INetMonster, telephony: ITelephonyManagerCompat): NetworkType? =
        when (telephony.getDisplayInfo().overrideNetworkType) {
            DisplayInfo.NetworkOverrideType.LTE_CA -> {
                NetworkTypeTable.get(NetworkType.LTE_CA)
            }
            DisplayInfo.NetworkOverrideType.NR_ADVANCED, DisplayInfo.NetworkOverrideType.NR_NSA -> {
                NetworkTypeTable.get(NetworkType.LTE_NR)
            }
            else -> null
        }

}