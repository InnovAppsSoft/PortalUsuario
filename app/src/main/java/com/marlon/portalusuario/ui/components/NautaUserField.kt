package com.marlon.portalusuario.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.ui.theme.PortalUsuarioTheme
import cu.suitetecsa.nautanav.commons.ui.AnimatedPlaceholder
import cu.suitetecsa.nautanav.ui.components.PrettyCard

@Composable
fun NautaUserField(user: String = "", enabled: Boolean = false, onChangedText: (String) -> Unit = {}) {
    TextField(
        value = user,
        onValueChange = onChangedText,
        placeholder = { AnimatedPlaceholder(hints = listOf("51234567", "61234567")) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Next
        ),
        leadingIcon = { Text(text = "+53") },
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(8.dp),
        enabled = enabled
    )
}

@Preview(showBackground = true)
@Composable
private fun NautaUserFieldPreview() {
    PortalUsuarioTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            PrettyCard { NautaUserField() }
        }
    }
}
