package com.marlon.portalusuario.nauta.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
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
fun CardRecharge(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    rechargeStatus: Pair<Boolean, String?>,
    rechargeCode: TextFieldValue,
    isExecutable: Boolean,
    onChangeRechargeCode: (TextFieldValue) -> Unit,
    onRecharge: (TextFieldValue) -> Unit
) {
    val (isOk, _) = rechargeStatus
    PrettyCard(modifier = modifier, isLoading = isLoading, isFoundErrors = !isOk) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            TextField(
                value = rechargeCode,
                onValueChange = { onChangeRechargeCode(it) },
                placeholder = {
                    AnimatedPlaceholder(
                        hints = listOf(
                            stringResource(R.string.new_recharge_text_field_placeholder_one),
                            stringResource(R.string.new_recharge_text_field_placeholder_two)
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(onGo = { if (isExecutable) onRecharge(rechargeCode) }),
                shape = RoundedCornerShape(
                    topStart = 8.dp,
                    topEnd = 0.dp,
                    bottomEnd = 0.dp,
                    bottomStart = 8.dp
                ),
                singleLine = true,
                maxLines = 1,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_numbers_24),
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.new_baseline_qr_code_scanner),
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardRechargePreview() {
    SuitEtecsaTheme {
        CardRecharge(
            isLoading = false,
            rechargeStatus = Pair(true, null),
            rechargeCode = TextFieldValue(""),
            isExecutable = false,
            onChangeRechargeCode = {},
            onRecharge = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CardRechargePreviewDark() {
    SuitEtecsaTheme {
        CardRecharge(
            isLoading = false,
            rechargeStatus = Pair(true, null),
            rechargeCode = TextFieldValue(""),
            isExecutable = false,
            onChangeRechargeCode = {},
            onRecharge = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}