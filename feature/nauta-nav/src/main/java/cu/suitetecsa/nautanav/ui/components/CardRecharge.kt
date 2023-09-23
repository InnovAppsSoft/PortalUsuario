package cu.suitetecsa.nautanav.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cu.suitetecsa.nauta_nav.R
import cu.suitetecsa.nautanav.commons.ui.AnimatedPlaceholder
import cu.suitetecsa.nautanav.commons.ui.theme.SuitEtecsaTheme
import cu.suitetecsa.nautanav.util.rechargeCodeVisualTransformation

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CardRecharge(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    rechargeStatus: Pair<Boolean, String?>,
    rechargeCode: String,
    isExecutable: Boolean,
    onChangeRechargeCode: (String) -> Unit,
    onRecharge: (String) -> Unit,
    onClickQRScannerIcon: () -> Unit
) {
    val (isOk, _) = rechargeStatus
    val keyboardController = LocalSoftwareKeyboardController.current

    if (rechargeCode.length == 16 && !isLoading) {
        keyboardController?.hide()
        onRecharge(rechargeCode)
    }
    PrettyCard(modifier = modifier, isLoading = isLoading, isFoundErrors = !isOk) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            TextField(
                value = rechargeCode,
                onValueChange = { code -> onChangeRechargeCode(code.takeWhile { it.isDigit() }) },
                visualTransformation = rechargeCodeVisualTransformation(),
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
                keyboardActions = KeyboardActions(
                    onGo = {
                        if (isExecutable) {
                            keyboardController?.hide()
                            onRecharge(rechargeCode)
                        }

                    }
                ),
                shape = MaterialTheme.shapes.small,
                singleLine = true,
                maxLines = 1,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Numbers,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { onClickQRScannerIcon() }) {
                        Icon(
                            imageVector = Icons.Outlined.QrCodeScanner,
                            contentDescription = null
                        )
                    }
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
            rechargeCode = "",
            isExecutable = false,
            onChangeRechargeCode = {},
            onRecharge = {},
            modifier = Modifier.padding(16.dp),
            onClickQRScannerIcon = {}
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
            rechargeCode = "",
            isExecutable = false,
            onChangeRechargeCode = {},
            onRecharge = {},
            modifier = Modifier.padding(16.dp),
            onClickQRScannerIcon = {}
        )
    }
}