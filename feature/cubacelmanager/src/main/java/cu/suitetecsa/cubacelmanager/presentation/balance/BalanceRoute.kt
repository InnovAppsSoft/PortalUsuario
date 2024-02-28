package cu.suitetecsa.cubacelmanager.presentation.balance

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cu.suitetecsa.cubacelmanager.R
import cu.suitetecsa.cubacelmanager.presentation.balance.components.topupbottomsheet.TopUpBottomSheet
import cu.suitetecsa.cubacelmanager.presentation.balance.components.topupbottomsheet.TopUpViewModel
import cu.suitetecsa.cubacelmanager.presentation.balance.components.transferfundsbottomsheet.TransferFundsBottomSheet
import cu.suitetecsa.cubacelmanager.presentation.balance.components.transferfundsbottomsheet.TransferFundsViewModel

@Composable
internal fun BalanceRoute(
    viewModel: BalanceViewModel = hiltViewModel(),
    topUpViewModel: TopUpViewModel = hiltViewModel(),
    transferFundsViewModel: TransferFundsViewModel = hiltViewModel(),
    onSetTitle: (String) -> Unit = {},
    onSetActions: (@Composable (RowScope.() -> Unit)) -> Unit = {},
    bottomPadding: PaddingValues = PaddingValues(),
    topPadding: PaddingValues = PaddingValues(),
) {
    onSetTitle(viewModel.state.value.runningMessage ?: stringResource(id = R.string.balance_credit))

    BalanceScreen(
        viewModel = viewModel,
        bottomPadding = bottomPadding,
        topPadding = topPadding,
        onSetActions = onSetActions
    )

    viewModel.state.value.currentSimCard?.let {
        TopUpBottomSheet(
            state = topUpViewModel.state.value,
            isVisible = viewModel.state.value.isTopUpSheetVisible,
            currentSimCard = it,
            onEvent = topUpViewModel::onEvent,
            onDismiss = { viewModel.onEvent(BalanceEvent.OnChangeTopUpSheetVisibility(false)) }
        )

        TransferFundsBottomSheet(
            state = transferFundsViewModel.state.value,
            currentSimCard = it,
            onEvent = transferFundsViewModel::onEvent,
            isVisible = viewModel.state.value.isTransferSheetVisible,
            onDismiss = { viewModel.onEvent(BalanceEvent.OnChangeTransferSheetVisibility(false)) }
        )
    }
}
