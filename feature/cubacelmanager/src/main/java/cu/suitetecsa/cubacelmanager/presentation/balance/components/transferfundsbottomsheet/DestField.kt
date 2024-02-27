package cu.suitetecsa.cubacelmanager.presentation.balance.components.transferfundsbottomsheet

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import cu.suitetecsa.core.ui.components.animateplaceholder.AnimatedPlaceholder
import cu.suitetecsa.cubacelmanager.R

@Composable
internal fun DestField(
    state: TransferFundsSheetState,
    onEvent: (TransferFundsSheetEvent) -> Unit,
    onContactSelect: () -> Unit,
) {
    TextField(
        value = state.dest,
        onValueChange = { onEvent(TransferFundsSheetEvent.OnChangeDest(it)) },
        enabled = !state.isLoading,
        placeholder = {
            AnimatedPlaceholder(
                hints = listOf(
                    stringResource(R.string.phone_number_to_transfer),
                    stringResource(R.string.phone_number_example)
                )
            )
        },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
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
                imageVector = Icons.Outlined.Phone,
                contentDescription = null
            )
        },
        trailingIcon = {
            Row {
                IconButton(onClick = onContactSelect) {
                    Icon(
                        imageVector = Icons.Outlined.Contacts,
                        contentDescription = null
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Outlined.QrCodeScanner,
                        contentDescription = null
                    )
                }
            }
        }
    )
}
