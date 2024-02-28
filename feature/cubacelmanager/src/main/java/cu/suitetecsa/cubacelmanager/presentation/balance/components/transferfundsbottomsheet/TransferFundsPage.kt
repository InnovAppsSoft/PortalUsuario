package cu.suitetecsa.cubacelmanager.presentation.balance.components.transferfundsbottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cu.suitetecsa.core.ui.theme.SuitEtecsaTheme
import cu.suitetecsa.sdk.android.model.SimCard

private const val FieldWight = .5f

@Composable
internal fun TransferFundsPage(
    state: TransferFundsSheetState,
    currentSimCard: SimCard,
    onEvent: (TransferFundsSheetEvent) -> Unit,
    onContactSelect: () -> Unit,
) {
    Column(Modifier.padding(8.dp)) {
        DestField(state = state, onEvent = onEvent, onContactSelect = onContactSelect)
        Spacer(modifier = Modifier.padding(4.dp))
        Row {
            AmountField(modifier = Modifier.weight(FieldWight), state = state, onEvent = onEvent)
            Spacer(modifier = Modifier.padding(4.dp))
            PinField(
                modifier = Modifier.weight(FieldWight),
                state = state,
                currentSimCard = currentSimCard,
                onEvent = onEvent
            )
        }
    }
}

@Preview
@Composable
private fun TransferFundsViewPreview() {
    SuitEtecsaTheme {
        Surface {
            TransferFundsPage(
                state = TransferFundsSheetState(),
                onEvent = {},
                currentSimCard = SimCard("", "", null, 0, 0, null)
            ) {}
        }
    }
}
