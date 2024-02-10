package cu.suitetecsa.core.ui.components.rechargeview

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
import androidx.compose.material3.Surface
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
import cu.suitetecsa.core.ui.R
import cu.suitetecsa.core.ui.components.animateplaceholder.AnimatedPlaceholder
import cu.suitetecsa.core.ui.components.prettycard.PrettyCard
import cu.suitetecsa.core.ui.theme.SuitEtecsaTheme

private const val MaxLengthTopUpCode = 16
private const val MinLengthTopUpCode = 12

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RechargeView(
    modifier: Modifier = Modifier,
    state: RechargeViewState = RechargeViewState(),
    canRun: Boolean = false,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    if (state.code.length == MaxLengthTopUpCode && !state.isLoading) {
        keyboardController?.hide()
        state.onTopUp()
    }
    PrettyCard(modifier = modifier, isLoading = state.isLoading) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            TextField(
                value = state.code,
                onValueChange = { code -> state.onChangeTopUpCode(code.takeWhile { it.isDigit() }) },
                visualTransformation = rechargeCodeVisualTransformation(),
                enabled = canRun,
                placeholder = {
                    AnimatedPlaceholder(
                        hints = listOf(
                            stringResource(R.string.recharge_code_placeholder),
                            stringResource(R.string.recharge_code_example)
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(onGo = {
                    if (!state.isLoading && canRun && state.code.length in listOf(
                            MinLengthTopUpCode, MaxLengthTopUpCode
                        )
                    ) {
                        keyboardController?.hide()
                        state.onTopUp()
                    }
                }),
                shape = MaterialTheme.shapes.small,
                singleLine = true,
                maxLines = 1,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Numbers, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = state.onClickQrIcon) {
                        Icon(imageVector = Icons.Outlined.QrCodeScanner, contentDescription = null)
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RechargeViewPreview() {
    SuitEtecsaTheme {
        RechargeView(
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, locale = "es")
@Composable
fun RechargeViewPreviewDark() {
    SuitEtecsaTheme {
        Surface {
            RechargeView(
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
