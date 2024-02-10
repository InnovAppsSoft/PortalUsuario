package cu.suitetecsa.cubacelmanager.presentation.balance

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cu.suitetecsa.cubacelmanager.R

@Composable
internal fun BalanceRoute(
    viewModel: BalanceViewModel = hiltViewModel(),
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
}
