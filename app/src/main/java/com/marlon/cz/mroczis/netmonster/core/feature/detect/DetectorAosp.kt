package com.marlon.cz.mroczis.netmonster.core.feature.detect

import android.Manifest
import androidx.annotation.RequiresPermission
import com.marlon.cz.mroczis.netmonster.core.INetMonster
import com.marlon.cz.mroczis.netmonster.core.db.model.NetworkType
import com.marlon.cz.mroczis.netmonster.core.telephony.ITelephonyManagerCompat

/**
 * Detection of current network type base of AOSP method.
 */
class DetectorAosp : INetworkDetector {

    @RequiresPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    override fun detect(netmonster: INetMonster, telephony: ITelephonyManagerCompat): NetworkType? =
        telephony.getNetworkType()


}