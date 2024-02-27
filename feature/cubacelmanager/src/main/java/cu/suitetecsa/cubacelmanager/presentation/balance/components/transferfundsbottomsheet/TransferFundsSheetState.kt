package cu.suitetecsa.cubacelmanager.presentation.balance.components.transferfundsbottomsheet

import cu.suitetecsa.sdk.android.model.Contact

data class TransferFundsSheetState(
    val dest: String = "",
    val amount: String = "",
    val pin: String = "",
    val isLoading: Boolean = false,
    val canTransfer: Boolean = false,
    val contacts: List<Contact> = listOf(),
)
