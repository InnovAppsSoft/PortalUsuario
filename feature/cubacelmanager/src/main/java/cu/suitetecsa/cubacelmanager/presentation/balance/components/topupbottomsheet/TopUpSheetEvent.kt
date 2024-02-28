package cu.suitetecsa.cubacelmanager.presentation.balance.components.topupbottomsheet

import cu.suitetecsa.sdk.android.model.SimCard

sealed class TopUpSheetEvent {
    data class OnTopUp(val simCard: SimCard) : TopUpSheetEvent()
    data class OnChangeCode(val code: String) : TopUpSheetEvent()
    data class OnThrowError(val message: String) : TopUpSheetEvent()
}
