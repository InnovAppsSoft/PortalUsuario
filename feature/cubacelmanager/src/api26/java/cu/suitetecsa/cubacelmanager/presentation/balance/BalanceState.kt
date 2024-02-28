package cu.suitetecsa.cubacelmanager.presentation.balance

import cu.suitetecsa.sdk.android.model.SimCard

data class BalanceState(
    val canRun: Boolean = false,
    val loading: Boolean = false,
    val runningMessage: String? = null,
    val simCards: List<SimCard> = listOf(),
    val currentSimCard: SimCard? = null,
    val resultMessage: String? = null,
    val isTopUpSheetVisible: Boolean = false,
    val isTransferSheetVisible: Boolean = false,
)
