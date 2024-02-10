package cu.suitetecsa.cubacelmanager.presentation.balance

import cu.suitetecsa.sdk.android.model.SimCard

sealed class BalanceEvent {
    data class ChangeSimCard(val simCard: SimCard) : BalanceEvent()
    data class ChangeRechargeCode(val code: String) : BalanceEvent()
    data object TopUpBalance : BalanceEvent()
    data class ChangeNumberToTransfer(val number: String) : BalanceEvent()
    data class ChangeAmountToTransfer(val amount: String) : BalanceEvent()
    data class ChangePinPassword(val pinPassword: String) : BalanceEvent()
    data object TransferFunds : BalanceEvent()
    data class MakeCall(val ussdCode: String) : BalanceEvent()
}
