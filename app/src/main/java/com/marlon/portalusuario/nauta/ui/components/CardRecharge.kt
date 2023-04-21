package com.marlon.portalusuario.nauta.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.runtime.Composable
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.marlon.portalusuario.R
import com.marlon.portalusuario.commons.ui.AnimatedPlaceholder

@Composable
fun CardRecharge(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    rechargeStatus: Pair<Boolean, String?>,
    rechargeCode: TextFieldValue,
    isRechargeButtonEnabled: Boolean,
    onChangeRechargeCode: (TextFieldValue) -> Unit,
    onRecharge: (TextFieldValue) -> Unit
) {
    val (isOk, errors) = rechargeStatus
    PrettyCard(modifier = modifier, isLoading = isLoading, isFoundErrors = !isOk) {
        Box {
            ConstraintLayout {
                val (inputRechargeCode, buttonRecharge) = createRefs()

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
                        .padding(top = 8.dp, bottom = 8.dp, start = 8.dp, end = 0.dp)
                        .constrainAs(inputRechargeCode) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            bottom.linkTo(parent.bottom)
                            end.linkTo(buttonRecharge.start)
                        },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions(onGo = { }),
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
                        Icon(imageVector = Icons.Default.Numbers, contentDescription = null)
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.new_baseline_qr_code_scanner),
                            contentDescription = null
                        )
                    }
                )
                Button(
                    onClick = { onRecharge(rechargeCode) },
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp, start = 0.dp, end = 8.dp)
                        .constrainAs(buttonRecharge) {
                            top.linkTo(inputRechargeCode.top)
                            start.linkTo(inputRechargeCode.end)
                            bottom.linkTo(inputRechargeCode.bottom)
                            end.linkTo(parent.end)
                            height = Dimension.fillToConstraints
                        },
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 8.dp,
                        bottomEnd = 8.dp,
                        bottomStart = 0.dp
                    ),
                    enabled = isRechargeButtonEnabled
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.new_baseline_send),
                        contentDescription = stringResource(R.string.new_recharge_content_description)
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun CardRechargePreview() {
    CardRecharge(
        isLoading = false,
        rechargeStatus = Pair(true, null),
        rechargeCode = TextFieldValue(""),
        isRechargeButtonEnabled = false,
        onChangeRechargeCode = {},
        onRecharge = {},
        modifier = Modifier.padding(16.dp)
    )
}