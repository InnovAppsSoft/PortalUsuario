package cu.suitetecsa.cubacelmanager.presentation.balance.components.transferfundsbottomsheet

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
internal fun AmountField(
    modifier: Modifier = Modifier,
    state: TransferFundsSheetState,
    onEvent: (TransferFundsSheetEvent) -> Unit,
) {
    TextField(
        value = state.amount,
        onValueChange = { onEvent(TransferFundsSheetEvent.OnChangeAmount(it)) },
        enabled = !state.isLoading,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Next
        ),
        shape = MaterialTheme.shapes.small,
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.AttachMoney,
                contentDescription = null
            )
        },
        trailingIcon = {
            IconButton(onClick = { }) {
                Text(text = "CUP")
            }
        }
    )
}
