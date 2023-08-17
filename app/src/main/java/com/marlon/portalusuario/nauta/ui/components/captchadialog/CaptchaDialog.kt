package com.marlon.portalusuario.nauta.ui.components.captchadialog

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.marlon.portalusuario.nauta.ui.components.PrettyCard
import com.marlon.portalusuario.nauta.ui.components.captchadialog.CaptchaDialogState.Hidden
import com.marlon.portalusuario.nauta.ui.components.captchadialog.CaptchaDialogState.Showing
import com.marlon.portalusuario.nauta.ui.components.captchaview.CaptchaView
import com.marlon.portalusuario.nauta.ui.components.captchaview.CaptchaViewState

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CaptchaDialog(
    state: CaptchaDialogState,
    captchaViewState: CaptchaViewState,
    captchaCode: String,
    onClickImage: () -> Unit,
    onChangeCaptchaCode: (String) -> Unit,
    actionRun: (String) -> Unit,
    onDismiss: () -> Unit
) {
    when (state) {
        Hidden -> {}
        is Showing -> {
            val keyboardController = LocalSoftwareKeyboardController.current
            if (state.isActionExecuting) keyboardController?.hide()

            Dialog(
                onDismissRequest = { onDismiss() },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                PrettyCard(isLoading = state.isActionExecuting) {
                    Column(
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        CaptchaView(
                            state = captchaViewState
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
                                    if (state.isExecutable) {
                                        keyboardController?.hide()
                                        actionRun(captchaCode)
                                    }
                                }
                            ),
                            singleLine = true,
                            maxLines = 1,
                            colors = TextFieldDefaults.textFieldColors(
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
}

@Preview(device = Devices.PIXEL_4, showBackground = true)
@Composable
fun CaptchaDialogPreview() {
    SuitEtecsaTheme {
        CaptchaDialog(
            state = Showing(
                isActionExecuting = false,
            ),
            captchaViewState = CaptchaViewState.Loading,
            captchaCode = "",
            onClickImage = { },
            onChangeCaptchaCode = {},
            actionRun = {}
        ) {}
    }
}

@Preview(device = Devices.PIXEL_4, showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CaptchaDialogPreviewDark() {
    SuitEtecsaTheme {
        CaptchaDialog(
            state = Showing(
                isActionExecuting = false,
            ),
            captchaViewState = CaptchaViewState.Loading,
            captchaCode = "",
            onClickImage = { },
            onChangeCaptchaCode = {},
            actionRun = {}
        ) {}
    }
}
