package com.marlon.portalusuario.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.marlon.portalusuario.R
import cu.suitetecsa.nautanav.commons.ui.AnimatedPlaceholder

@Composable
fun CaptchaField(value: String, enabled: Boolean = false, onChangedValue: (String) -> Unit, onDone: () -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    TextField(
        value = value,
        onValueChange = onChangedValue,
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
                keyboardController?.hide()
                onDone()
            }
        ),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = MaterialTheme.shapes.small,
        enabled = enabled
    )
}
