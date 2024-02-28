package cu.suitetecsa.cubacelmanager.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cu.suitetecsa.core.ui.components.Spinner
import cu.suitetecsa.sdk.android.model.SimCard

@Composable
internal fun SimCardsSpinner(
    simCards: List<SimCard?>,
    currentSimCard: SimCard?,
    onSimCardSelect: (SimCard?) -> Unit,
    canRun: Boolean,
    simCardIcons: List<ImageVector>
) {
    Spinner(
        items = simCards,
        selectedItem = currentSimCard,
        onItemSelect = onSimCardSelect,
        enabled = canRun,
        selectedItemFactory = { modifier, item ->
            Row(
                modifier = modifier
                    .padding(8.dp)
                    .wrapContentSize()
            ) {
                item?.let {
                    Icon(
                        imageVector = simCardIcons[it.slotIndex],
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        dropdownItemFactory = { item, _ ->
            item?.let {
                Row {
                    Icon(
                        imageVector = simCardIcons[it.slotIndex],
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(text = " ${it.displayName}")
                }
            }
        }
    )
}
