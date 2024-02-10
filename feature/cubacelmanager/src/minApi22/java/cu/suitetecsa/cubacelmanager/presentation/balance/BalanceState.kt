package cu.suitetecsa.cubacelmanager.presentation.balance

import cu.suitetecsa.core.ui.components.rechargeview.RechargeViewState
import cu.suitetecsa.cubacelmanager.presentation.balance.components.cardtransfer.CardTransferState
import cu.suitetecsa.sdk.android.model.SimCard

data class BalanceState(
    val canRun: Boolean = false,
    val rechargeState: RechargeViewState = RechargeViewState(),
    val transferState: CardTransferState = CardTransferState(),
    val simCards: List<SimCard> = listOf(),
    val currentSimCard: SimCard? = null,
    val runningMessage: String? = null,
)
