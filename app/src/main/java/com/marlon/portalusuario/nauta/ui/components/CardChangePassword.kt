package com.marlon.portalusuario.nauta.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.R
import com.marlon.portalusuario.commons.ui.AnimatedPlaceholder
import com.marlon.portalusuario.commons.ui.theme.SuitEtecsaTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CardChangePassword(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    newPassword: String,
    changedPasswordStatus: Pair<Boolean, String?>,
    isExecutable: Boolean,
    onChangeNewPassword: (String) -> Unit,
    onChangePassword: (String) -> Unit,
    onGeneratePassword: () -> Unit
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val (isChanged, _) = changedPasswordStatus
    val keyboardController = LocalSoftwareKeyboardController.current

    PrettyCard(modifier = modifier, isLoading = isLoading, isFoundErrors = !isChanged) {
        TextField(
            value = newPassword,
            onValueChange = { onChangeNewPassword(it) },
            placeholder = {
                AnimatedPlaceholder(
                    hints = listOf(
                        stringResource(R.string.change_password),
                        stringResource(R.string.new_password_example)
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(
                onGo = { if (isExecutable) {
                    keyboardController?.hide()
                    onChangePassword(newPassword)
                } }
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = MaterialTheme.shapes.small,
            singleLine = true,
            maxLines = 1,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = stringResource(R.string.change_password)
                )
            },
            trailingIcon = {
                Row {
                    IconButton(onClick = { onGeneratePassword() }) {
                        Icon(
                            imageVector = Icons.Outlined.Autorenew,
                            contentDescription = stringResource(R.string.generate_password)
                        )
                    }
                    val image = if (passwordVisible) Icons.Outlined.Visibility
                    else Icons.Outlined.VisibilityOff

                    val description =
                        if (passwordVisible) stringResource(R.string.hide_password) else stringResource(
                            R.string.show_password
                        )

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CardRechargePasswordPreview() {
    SuitEtecsaTheme {
        CardChangePassword(
            modifier = Modifier.padding(16.dp),
            isLoading = true,
            newPassword = "123456",
            changedPasswordStatus = Pair(true, null),
            isExecutable = true,
            onChangeNewPassword = { },
            onChangePassword = { },
            onGeneratePassword = { }
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CardRechargePasswordPreviewDark() {
    SuitEtecsaTheme {
        CardChangePassword(
            modifier = Modifier.padding(16.dp),
            isLoading = true,
            newPassword = "123456",
            changedPasswordStatus = Pair(true, null),
            isExecutable = true,
            onChangeNewPassword = { },
            onChangePassword = { },
            onGeneratePassword = { }
        )
    }
}