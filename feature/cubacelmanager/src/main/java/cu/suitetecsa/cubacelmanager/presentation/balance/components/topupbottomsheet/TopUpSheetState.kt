package cu.suitetecsa.cubacelmanager.presentation.balance.components.topupbottomsheet

data class TopUpSheetState(
    val topUpCode: String = "",
    val isLoading: Boolean = false,
    val canTopUp: Boolean = false,
    val resultMessage: String? = null,
)
