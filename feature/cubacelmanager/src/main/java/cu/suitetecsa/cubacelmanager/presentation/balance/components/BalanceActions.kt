package cu.suitetecsa.cubacelmanager.presentation.balance.components

import android.os.Build
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.SimCardAlert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cu.suitetecsa.cubacelmanager.R
import cu.suitetecsa.cubacelmanager.ui.components.SimCardsSpinner
import cu.suitetecsa.sdk.android.model.SimCard

@Composable
internal fun BalanceActions(
    simCards: List<SimCard?> = listOf(),
    currentSimCard: SimCard? = null,
    onSimCardSelect: (SimCard?) -> Unit = {},
    onBalanceUpdate: () -> Unit = {},
    canRun: Boolean = false,
    isLoading: Boolean = false,
) {
    if (simCards.isNotEmpty()) {
        val simCardIcons = listOf(
            ImageVector.vectorResource(id = R.drawable.sim_one),
            ImageVector.vectorResource(id = R.drawable.sim_two),
            ImageVector.vectorResource(id = R.drawable.sim_three)
        )
        if (simCards.size > 1) {
            SimCardsSpinner(simCards, currentSimCard, onSimCardSelect, canRun, simCardIcons)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(32.dp)
                )
            } else {
                IconButton(onClick = onBalanceUpdate, enabled = canRun) {
                    Icon(imageVector = Icons.Outlined.Autorenew, contentDescription = "Update")
                }
            }
        }
    } else {
        Icon(imageVector = Icons.Outlined.SimCardAlert, contentDescription = null)
    }
}

@Preview
@Composable
private fun BalanceActionsPreview() {
    val simCards = listOf(
        SimCard("", "", 1, 1, null),
        SimCard("ih", "", 2, 2, null)
    )
    MaterialTheme {
        Surface {
            Row {
                BalanceActions(
                    simCards = simCards,
                    currentSimCard = simCards[0],
                    isLoading = true
                )
            }
        }
    }
}
