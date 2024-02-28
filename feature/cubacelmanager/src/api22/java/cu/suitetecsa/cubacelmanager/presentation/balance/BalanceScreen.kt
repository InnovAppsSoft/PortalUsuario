package cu.suitetecsa.cubacelmanager.presentation.balance

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cu.suitetecsa.cubacelmanager.presentation.balance.components.BalanceActions
import cu.suitetecsa.cubacelmanager.presentation.balance.components.BalanceManager

@Composable
internal fun BalanceScreen(
    viewModel: BalanceViewModel,
    bottomPadding: PaddingValues,
    topPadding: PaddingValues,
    onSetActions: (@Composable (RowScope.() -> Unit)) -> Unit = {},
) {
    onSetActions {
        BalanceActions(
            simCards = viewModel.state.value.simCards,
            currentSimCard = viewModel.state.value.currentSimCard,
            onSimCardSelect = {
                it?.also { simCard ->
                    viewModel.onEvent(BalanceEvent.ChangeSimCard(simCard))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottomPadding)
    ) {
        Box(modifier = Modifier.height(topPadding.calculateTopPadding())) {}
        BalanceManager(
            modifier = Modifier.padding(16.dp),
            onUssdCall = viewModel::onEvent
        )
    }
}
