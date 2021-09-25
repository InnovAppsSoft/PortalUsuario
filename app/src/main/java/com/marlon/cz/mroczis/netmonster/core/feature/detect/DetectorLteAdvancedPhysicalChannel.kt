package com.marlon.cz.mroczis.netmonster.core.feature.detect

import android.os.Build
import com.marlon.cz.mroczis.netmonster.core.INetMonster
import com.marlon.cz.mroczis.netmonster.core.db.NetworkTypeTable
import com.marlon.cz.mroczis.netmonster.core.db.model.NetworkType
import com.marlon.cz.mroczis.netmonster.core.model.annotation.SinceSdk
import com.marlon.cz.mroczis.netmonster.core.model.connection.SecondaryConnection
import com.marlon.cz.mroczis.netmonster.core.telephony.ITelephonyManagerCompat

/**
 * Attempts to detect LTE Advanced / LTE Carrier aggregation
 *
 * Based on different physical channel configuration (PCC) which was added in [Build.VERSION_CODES.P].
 * PCC tells us whether are there multiple serving LTE cells. Once there's any secondary we known that
 * this is LTE-A for sure.
 */
class DetectorLteAdvancedPhysicalChannel : INetworkDetector {

    @SinceSdk(Build.VERSION_CODES.P)
    override fun detect(netmonster: INetMonster, telephony: ITelephonyManagerCompat): NetworkType? =
        netmonster.getPhysicalChannelConfiguration(telephony.getSubscriberId()).let { pcc ->
            if (pcc.size > 1 && pcc.firstOrNull { it.connectionStatus is SecondaryConnection } != null) {
                NetworkTypeTable.get(NetworkType.LTE_CA)
            } else null
        }

}