package cu.suitetecsa.nautanav.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun CameraPermissionRequestDialog(show: Boolean, onDismiss: () -> Unit) {
    if (show) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(text = "Se requiere la cámara") },
            text = {
                Text(
                    text = "Se requiere la cámara, para poder escanear el código QR"
                )
            },
            confirmButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text(text = "Ok")
                }
            }
        )
    }
}