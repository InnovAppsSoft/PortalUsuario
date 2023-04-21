package com.marlon.portalusuario.nauta.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.R
import com.marlon.portalusuario.commons.ui.AnimatedPlaceholder

@Composable
fun CardTransfer(
    modifier: Modifier = Modifier,
    destinationAccount: TextFieldValue,
    amount: TextFieldValue,
    transferButtonEnabled: Boolean,
    isLoading: Boolean,
    transferStatus: Pair<Boolean, String?>,
    onTransfer: (destinationAccount: String, amount: String) -> Unit,
    onChangeDestinationAccount: (TextFieldValue) -> Unit,
    onChangeAmount: (TextFieldValue) -> Unit
) {
    val (isOk, errors) = transferStatus
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
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.padding(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
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
                        .weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    maxLines = 1,
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                )
                Button(
                    onClick = { onTransfer(amount.text, destinationAccount.text) },
                    shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                    modifier = Modifier.fillMaxHeight(),
                    enabled = transferButtonEnabled
                ) {
                    Text(text = stringResource(id = R.string.recharge))
                }
            }
        }
    }
}

@Preview
@Composable
fun CardTransferPreview() {
    CardTransfer(
        destinationAccount = TextFieldValue(""),
        amount = TextFieldValue(""),
        transferButtonEnabled = false,
        isLoading = false,
        transferStatus = Pair(true, null),
        onTransfer = { _, _ -> },
        onChangeDestinationAccount = {},
        onChangeAmount = {}
    )
}