package com.marlon.cz.mroczis.netmonster.core.telephony.network

import com.marlon.cz.mroczis.netmonster.core.model.Network
import com.marlon.cz.mroczis.netmonster.core.telephony.ITelephonyManagerCompat

/**
 * Interface that allows us obtain [Network].
 */
interface INetworkGetter {

    /**
     * Fetches network from using [telephony]
     * @return network or null
     */
    fun getNetwork(telephony: ITelephonyManagerCompat): Network?
}