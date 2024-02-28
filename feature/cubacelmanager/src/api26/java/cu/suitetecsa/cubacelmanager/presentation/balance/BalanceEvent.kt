package cu.suitetecsa.cubacelmanager.presentation.balance

import cu.suitetecsa.sdk.android.model.SimCard

sealed class BalanceEvent {
    data class ChangeSimCard(val simCard: SimCard) : BalanceEvent()
    data class TurnUsageBasedPricing(val active: Boolean) : BalanceEvent()
    data object UpdateBalance : BalanceEvent()
    data object DismissDialog : BalanceEvent()
    data class OnChangeTopUpSheetVisibility(val visible: Boolean) : BalanceEvent()
    data class OnChangeTransferSheetVisibility(val visible: Boolean) : BalanceEvent()
}