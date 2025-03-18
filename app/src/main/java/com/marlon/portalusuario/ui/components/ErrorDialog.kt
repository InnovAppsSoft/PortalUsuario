package com.marlon.portalusuario.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.marlon.portalusuario.R
import com.marlon.portalusuario.ui.theme.PortalUsuarioTheme

@Composable
fun ErrorDialog(
    errorText: String = "Error",
    onDismiss: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            decorFitsSystemWindows = false
        )
    ) {
        PrettyCard(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Column {
                Text(
                    text = stringResource(R.string.error),
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = errorText,
                    modifier = Modifier.padding(16.dp)
                )
                TextButton(
                    onClick = onDismiss, modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp)
                ) {
                    Text(text = stringResource(R.string.accept))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorDialogPreview() {
    PortalUsuarioTheme {
        ErrorDialog()
    }
}
