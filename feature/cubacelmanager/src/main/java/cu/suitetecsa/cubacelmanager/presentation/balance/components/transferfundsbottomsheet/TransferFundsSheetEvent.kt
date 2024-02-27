package cu.suitetecsa.cubacelmanager.presentation.balance.components.transferfundsbottomsheet

import cu.suitetecsa.sdk.android.model.SimCard

sealed class TransferFundsSheetEvent {
    data class OnChangeDest(val dest: String) : TransferFundsSheetEvent()
    data class OnChangeAmount(val amount: String) : TransferFundsSheetEvent()
    data class OnChangePin(val pin: String) : TransferFundsSheetEvent()
    data class OnTransferFunds(val simCard: SimCard) : TransferFundsSheetEvent()
    data object OnCollectContacts : TransferFundsSheetEvent()
}
