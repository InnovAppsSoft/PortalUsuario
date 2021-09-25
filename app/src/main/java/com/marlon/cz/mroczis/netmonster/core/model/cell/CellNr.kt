package com.marlon.cz.mroczis.netmonster.core.model.cell

import android.os.Build
import com.marlon.cz.mroczis.netmonster.core.model.Network
import com.marlon.cz.mroczis.netmonster.core.model.annotation.SinceSdk
import com.marlon.cz.mroczis.netmonster.core.model.band.BandNr
import com.marlon.cz.mroczis.netmonster.core.model.cell.CellNr.Companion.CID_MAX
import com.marlon.cz.mroczis.netmonster.core.model.cell.CellNr.Companion.CID_MIN
import com.marlon.cz.mroczis.netmonster.core.model.cell.CellNr.Companion.PCI_MAX
import com.marlon.cz.mroczis.netmonster.core.model.cell.CellNr.Companion.PCI_MIN
import com.marlon.cz.mroczis.netmonster.core.model.cell.CellNr.Companion.TAC_MAX
import com.marlon.cz.mroczis.netmonster.core.model.cell.CellNr.Companion.TAC_MIN
import com.marlon.cz.mroczis.netmonster.core.model.connection.IConnection
import com.marlon.cz.mroczis.netmonster.core.model.signal.SignalNr

@SinceSdk(Build.VERSION_CODES.Q)
data class CellNr(
        override val network: Network?,

        /**
     * 36-bit NR Cell Identity
     * in range from [CID_MIN] to [CID_MAX], null if unavailable
     */
    val nci: Long?,

        /**
     * 24-bit Tracking Area Code
     * in range from [TAC_MIN] to [TAC_MAX], null if unavailable
     */
    val tac: Int?,

        /**
     * 10-bit Physical Cell Id
     * in range from [PCI_MIN] to [PCI_MAX], null if unavailable
     */
    val pci: Int?,

        override val band: BandNr?,
        override val signal: SignalNr,
        override val connectionStatus: IConnection,
        override val subscriptionId: Int
) : ICell {

    override fun <T> let(processor: ICellProcessor<T>): T = processor.processNr(this)

    companion object {

        const val CID_MIN = 0L
        const val CID_MAX = 68_719_476_735L


        const val TAC_MIN = 0L
        const val TAC_MAX = 16_777_215L

        const val PCI_MIN = 0L
        const val PCI_MAX = 1007L

        internal val CID_RANGE = CID_MIN..CID_MAX
        internal val TAC_RANGE = TAC_MIN..TAC_MAX
        internal val PCI_RANGE = PCI_MIN..PCI_MAX
    }

}