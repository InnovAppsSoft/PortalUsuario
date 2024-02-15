package cu.suitetecsa.cubacelmanager.presentation.balance.components.cardtransfer

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cu.suitetecsa.core.ui.components.animateplaceholder.AnimatedPlaceholder
import cu.suitetecsa.core.ui.components.prettycard.PrettyCard
import cu.suitetecsa.core.ui.theme.SuitEtecsaTheme
import cu.suitetecsa.cubacelmanager.R

private const val FieldWight = .5f

@Composable
internal fun CardTransferCredit(
    modifier: Modifier = Modifier,
    canRun: Boolean = false,
    state: CardTransferState = CardTransferState(),
) {
    PrettyCard(modifier = modifier, isLoading = state.isLoading) {
        Column {
            DestField(state, canRun)
            Spacer(modifier = Modifier.padding(3.dp))
            Row {
                AmountField(Modifier.weight(FieldWight), state, canRun)
                Spacer(modifier = Modifier.padding(3.dp))
                PinPasswordField(Modifier.weight(FieldWight), state, canRun)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun PinPasswordField(
    modifier: Modifier = Modifier,
    state: CardTransferState,
    canRun: Boolean,
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = state.pinPassword,
        onValueChange = state.onChangePinPassword,
        enabled = canRun,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onGo = {
                if (state.dest.length == 8 && !state.isLoading && canRun) {
                    keyboardController?.hide()
                    state.onTransfer()
                }
            }
        ),
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
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
                imageVector = Icons.Outlined.Lock,
                contentDescription = null
            )
        },
        trailingIcon = {
            val image = if (passwordVisible) {
                Icons.Outlined.Visibility
            } else {
                Icons.Outlined.VisibilityOff
            }

            val description =
                if (passwordVisible) {
                    stringResource(R.string.hide_password)
                } else {
                    stringResource(
                        R.string.show_password
                    )
                }

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = description)
            }
        }
    )
}

@Composable
private fun AmountField(
    modifier: Modifier = Modifier,
    state: CardTransferState,
    canRun: Boolean,
) {
    TextField(
        value = state.amount,
        onValueChange = state.onChangeAmount,
        enabled = canRun,
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

@Composable
private fun DestField(state: CardTransferState, canRun: Boolean) {
    TextField(
        value = state.dest,
        onValueChange = state.onChangeDest,
        enabled = canRun,
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
                IconButton(onClick = state.onClickContactIcon) {
                    Icon(
                        imageVector = Icons.Outlined.Contacts,
                        contentDescription = null
                    )
                }
                IconButton(onClick = state.onClickQrIcon) {
                    Icon(
                        imageVector = Icons.Outlined.QrCodeScanner,
                        contentDescription = null
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
internal fun CardTransferCreditPreview() {
    SuitEtecsaTheme {
        CardTransferCredit(
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "es")
@Composable
internal fun CardTransferCreditPreviewDark() {
    SuitEtecsaTheme {
        Surface {
            CardTransferCredit(
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
