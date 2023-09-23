package cu.suitetecsa.nautanav.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cu.suitetecsa.nauta_nav.R
import cu.suitetecsa.nautanav.commons.ui.AnimatedPlaceholder
import cu.suitetecsa.nautanav.commons.ui.theme.SuitEtecsaTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CardChangeEmailPassword(
    modifier: Modifier = Modifier,
    oldPassword: String,
    newPassword: String,
    onChangePassword: (String, String) -> Unit,
    onChangeEmailPassword: (String, String) -> Unit,
    onGeneratePassword: () -> Unit,
    isChangingEmailPassword: Boolean,
    changePasswordStatus: Pair<Boolean, String?>
) {
    var oldPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var newPasswordVisible by rememberSaveable { mutableStateOf(false) }
    val (isPasswordChanged, errorMessage) = changePasswordStatus
    val keyboardController = LocalSoftwareKeyboardController.current

    if (!isPasswordChanged) Toast.makeText(LocalContext.current, errorMessage, Toast.LENGTH_LONG)
        .show()
    PrettyCard(
        modifier = modifier,
        isLoading = isChangingEmailPassword,
        isFoundErrors = !isPasswordChanged
    ) {
        Column {
            TextField(
                value = oldPassword,
                onValueChange = { onChangePassword(it, newPassword) },
                placeholder = {
                    AnimatedPlaceholder(
                        hints = listOf(
                            stringResource(R.string.current_password_placeholder),
                            stringResource(R.string.current_password_example)
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                visualTransformation = if (oldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = stringResource(id = R.string.password_placeholder)
                    )
                },
                trailingIcon = {
                    val image = if (oldPasswordVisible) Icons.Outlined.Visibility
                    else Icons.Outlined.VisibilityOff

                    val description =
                        if (oldPasswordVisible) stringResource(R.string.hide_password) else stringResource(
                            R.string.show_password
                        )

                    IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },
                singleLine = true,
                maxLines = 1,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = MaterialTheme.shapes.small
            )
            Spacer(modifier = Modifier.padding(4.dp))
            TextField(
                value = newPassword,
                onValueChange = { onChangePassword(oldPassword, it) },
                placeholder = {
                    AnimatedPlaceholder(
                        hints = listOf(
                            stringResource(R.string.new_password_placeholder),
                            stringResource(R.string.new_password_example)
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(onGo = {
                    keyboardController?.hide()
                    onChangeEmailPassword(
                        oldPassword,
                        newPassword
                    )
                }),
                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = stringResource(id = R.string.password_placeholder)
                    )
                },
                trailingIcon = {
                    Row {
                        IconButton(onClick = { onGeneratePassword() }) {
                            Icon(
                                imageVector = Icons.Outlined.Autorenew,
                                contentDescription = stringResource(
                                    id = R.string.generate_password
                                )
                            )
                        }
                        val image = if (newPasswordVisible) Icons.Outlined.Visibility
                        else Icons.Outlined.VisibilityOff

                        val description =
                            if (newPasswordVisible) stringResource(R.string.hide_password) else stringResource(
                                R.string.show_password
                            )

                        IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    }
                },
                singleLine = true,
                maxLines = 1,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = MaterialTheme.shapes.small
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardChangeEmailPasswordPreview() {
    SuitEtecsaTheme {
        CardChangeEmailPassword(
            modifier = Modifier.padding(16.dp),
            oldPassword = stringResource(id = R.string.current_password_example),
            newPassword = stringResource(id = R.string.new_password_example),
            onChangePassword = { _, _ -> },
            onChangeEmailPassword = { _, _ -> },
            onGeneratePassword = { /*TODO*/ },
            isChangingEmailPassword = false,
            changePasswordStatus = Pair(true, null),
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CardChangeEmailPasswordPreviewDark() {
    SuitEtecsaTheme {
        CardChangeEmailPassword(
            modifier = Modifier.padding(16.dp),
            oldPassword = stringResource(id = R.string.current_password_example),
            newPassword = stringResource(id = R.string.new_password_example),
            onChangePassword = { _, _ -> },
            onChangeEmailPassword = { _, _ -> },
            onGeneratePassword = { /*TODO*/ },
            isChangingEmailPassword = false,
            changePasswordStatus = Pair(true, null),
        )
    }
}