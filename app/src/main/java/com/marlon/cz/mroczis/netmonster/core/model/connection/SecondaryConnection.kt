package com.marlon.cz.mroczis.netmonster.core.model.connection

import com.marlon.cz.mroczis.netmonster.core.model.connection.IConnection

/**
 * Secondary cell for data communication (aggregated cell).
 *
 */
data class SecondaryConnection(
    /**
     * true = Guess made by NetMonster Core based on other indicators from RIL.
     *
     * false = Reported directly from device's RIL.
     */
    val isGuess: Boolean
) : IConnection