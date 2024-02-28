package cu.suitetecsa.core.ui.components.rechargeview

data class RechargeViewState(
    val code: String = "",
    val isLoading: Boolean = false,
    val onTopUp: () -> Unit = {},
    val onChangeTopUpCode: (String) -> Unit = {},
    val onClickQrIcon: () -> Unit = {},
)
