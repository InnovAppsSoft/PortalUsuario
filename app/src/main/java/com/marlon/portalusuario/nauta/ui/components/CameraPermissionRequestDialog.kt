package com.marlon.portalusuario.nauta.ui.components

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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