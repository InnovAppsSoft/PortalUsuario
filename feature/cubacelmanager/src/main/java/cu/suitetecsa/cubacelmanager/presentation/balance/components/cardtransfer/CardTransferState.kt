package cu.suitetecsa.cubacelmanager.presentation.balance.components.cardtransfer

data class CardTransferState(
    val dest: String = "",
    val amount: String = "",
    val pinPassword: String = "",
    val isLoading: Boolean = false,
    val onChangeDest: (String) -> Unit = {},
    val onChangeAmount: (String) -> Unit = {},
    val onChangePinPassword: (String) -> Unit = {},
    val onClickContactIcon: () -> Unit = {},
    val onClickQrIcon: () -> Unit = {},
    val onTransfer: () -> Unit = {}
)
