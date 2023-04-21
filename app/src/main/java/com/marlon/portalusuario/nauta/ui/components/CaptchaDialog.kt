package com.marlon.portalusuario.nauta.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Surface
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.marlon.portalusuario.R
import com.marlon.portalusuario.commons.ui.AnimatedPlaceholder

@Composable
fun CaptchaDialog(
    captchaImage: Bitmap?,
    captchaCode: TextFieldValue,
    isLoading: Boolean,
    updateButtonIsEnabled: Boolean,
    captchaLoadStatus: Pair<Boolean, String?>,
    onClickImage: () -> Unit,
    onChangeCaptchaCode: (TextFieldValue) -> Unit,
    onClickButton: (TextFieldValue) -> Unit,
    onDismiss: () -> Unit
) {
    val (isOk, _) = captchaLoadStatus
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        PrettyCard(isLoading = isLoading, isFoundErrors = !isOk) {
            Column(
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                CaptchaCanvas(
                    isLoading = isLoading,
                    captchaLoadStatus = captchaLoadStatus,
                    captchaImage = captchaImage
                ) {
                    onClickImage()
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    TextField(
                        value = captchaCode, onValueChange = { onChangeCaptchaCode(it) },
                        placeholder = {
                            AnimatedPlaceholder(
                                hints = listOf(
                                    stringResource(R.string.captcha_code),
                                    "HL46Fr"
                                )
                            )
                        },
                        modifier = Modifier
                            .weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
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
                        onClick = { onClickButton(captchaCode) },
                        shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                        modifier = Modifier.fillMaxHeight(),
                        enabled = updateButtonIsEnabled
                    ) {
                        Text(text = stringResource(R.string.update))
                    }
                }
            }
        }
    }
}

@Preview(device = Devices.PIXEL_4, showSystemUi = true)
@Composable
fun CaptchaDialogPreview() {
    CaptchaDialog(
        captchaImage = null,
        captchaCode = TextFieldValue(""),
        isLoading = false,
        updateButtonIsEnabled = false,
        captchaLoadStatus = Pair(false, null),
        onClickImage = { /*TODO*/ },
        onChangeCaptchaCode = {},
        onClickButton = {}
    ) {

    }
}