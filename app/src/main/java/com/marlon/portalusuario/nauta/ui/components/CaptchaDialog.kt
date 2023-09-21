package com.marlon.portalusuario.nauta.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.marlon.portalusuario.R
import com.marlon.portalusuario.commons.ui.AnimatedPlaceholder
import com.marlon.portalusuario.commons.ui.theme.SuitEtecsaTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CaptchaDialog(
    show: Boolean,
    captchaImage: Bitmap?,
    captchaCode: String,
    isLoading: Boolean,
    isLoadingCaptcha: Boolean,
    isExecutable: Boolean,
    captchaLoadStatus: Pair<Boolean, String?>,
    onClickImage: () -> Unit,
    onChangeCaptchaCode: (String) -> Unit,
    loginFunction: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (show) {
        val (isOk, err) = captchaLoadStatus
        if (!isOk) {
            Toast.makeText(LocalContext.current, err, Toast.LENGTH_LONG).show()
        }
        val keyboardController = LocalSoftwareKeyboardController.current
        if (isLoading) {
            keyboardController?.hide()
        }
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            PrettyCard(isLoading = isLoading, isFoundErrors = !isOk) {
                Column(
                    modifier = Modifier.widthIn(max = 280.dp)
                ) {
                    CaptchaCanvas(
                        isLoading = isLoadingCaptcha,
                        captchaLoadStatus = captchaLoadStatus,
                        captchaImage = captchaImage
                    ) {
                        onClickImage()
                    }
                    Spacer(modifier = Modifier.padding(4.dp))
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
                            .fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (isExecutable) {
                                    keyboardController?.hide()
                                    loginFunction(captchaCode)
                                }
                            }
                        ),
                        singleLine = true,
                        maxLines = 1,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }
    }
}

@Preview(device = Devices.PIXEL_4, showBackground = true)
@Composable
fun CaptchaDialogPreview() {
    SuitEtecsaTheme {
        CaptchaDialog(
            show = true,
            captchaImage = null,
            captchaCode = "",
            isLoading = false,
            isLoadingCaptcha = false,
            isExecutable = false,
            captchaLoadStatus = Pair(false, null),
            onClickImage = { /*TODO*/ },
            onChangeCaptchaCode = {},
            loginFunction = {}
        ) {}
    }
}

@Preview(device = Devices.PIXEL_4, showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CaptchaDialogPreviewDark() {
    SuitEtecsaTheme {
        CaptchaDialog(
            show = true,
            captchaImage = null,
            captchaCode = "",
            isLoading = false,
            isLoadingCaptcha = false,
            isExecutable = false,
            captchaLoadStatus = Pair(false, null),
            onClickImage = { /*TODO*/ },
            onChangeCaptchaCode = {},
            loginFunction = {}
        ) {}
    }
}