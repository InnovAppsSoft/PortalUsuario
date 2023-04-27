package com.marlon.portalusuario.nauta.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.R
import com.marlon.portalusuario.commons.ui.AnimatedPlaceholder
import com.marlon.portalusuario.commons.ui.theme.SuitEtecsaTheme

@Composable
fun CardTransfer(
    modifier: Modifier = Modifier,
    destinationAccount: TextFieldValue,
    amount: TextFieldValue,
    isExecutable: Boolean,
    isLoading: Boolean,
    transferStatus: Pair<Boolean, String?>,
    onTransfer: (destinationAccount: String, amount: String) -> Unit,
    onChangeDestinationAccount: (TextFieldValue) -> Unit,
    onChangeAmount: (TextFieldValue) -> Unit
) {
    val (isOk, _) = transferStatus
    PrettyCard(modifier = modifier, isLoading = isLoading, isFoundErrors = !isOk) {
        Column {
            TextField(
                value = destinationAccount,
                onValueChange = { onChangeDestinationAccount(it) },
                placeholder = {
                    AnimatedPlaceholder(
                        hints = listOf(
                            "user.name@nauta.com.cu", "user.name@nauta.co.cu"
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                ),
                singleLine = true,
                maxLines = 1,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null
                    )
                }
            )
            Spacer(modifier = Modifier.padding(2.dp))
            TextField(
                value = amount,
                onValueChange = { onChangeAmount(it) },
                placeholder = {
                    AnimatedPlaceholder(
                        hints = listOf(
                            stringResource(R.string.amount),
                            "40.00"
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onTransfer(destinationAccount.text, amount.text)
                    }
                ),
                singleLine = true,
                maxLines = 1,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp),
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_attach_money_24),
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardTransferPreview() {
    SuitEtecsaTheme {
        CardTransfer(
            destinationAccount = TextFieldValue(""),
            amount = TextFieldValue(""),
            isExecutable = false,
            isLoading = false,
            transferStatus = Pair(true, null),
            onTransfer = { _, _ -> },
            onChangeDestinationAccount = {},
            onChangeAmount = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CardTransferPreviewDark() {
    SuitEtecsaTheme {
        CardTransfer(
            destinationAccount = TextFieldValue(""),
            amount = TextFieldValue(""),
            isExecutable = false,
            isLoading = false,
            transferStatus = Pair(true, null),
            onTransfer = { _, _ -> },
            onChangeDestinationAccount = {},
            onChangeAmount = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}