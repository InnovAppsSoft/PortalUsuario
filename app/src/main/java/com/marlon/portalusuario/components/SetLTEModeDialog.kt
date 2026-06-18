package com.marlon.portalusuario.components

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.marlon.portalusuario.R

@Composable
fun ShowSetLTEModeDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configurar modo Solo LTE") },
        text = { Text(context.getString(R.string.se_al1)) },
        confirmButton = {
            Button(onClick = {
                try {
                    val intent = Intent("android.intent.action.MAIN").apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            setClassName("com.android.phone", "com.android.phone.settings.RadioInfo")
                        } else {
                            setClassName("com.android.settings", "com.android.settings.RadioInfo")
                        }
                    }
                    context.startActivity(intent)
                    onDismiss()
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, R.string.error_message_unsupported_feature, Toast.LENGTH_SHORT).show()
                    Log.e("SetLTEModeDialog", "Failed to open hidden menu", e)
                } catch (e: SecurityException) {
                    Toast.makeText(context, R.string.error_message_security_issue, Toast.LENGTH_SHORT).show()
                    Log.e("SetLTEModeDialog", "Security issue when trying to open hidden menu", e)
                }
            }) {
                Text("Configurar solo 4G")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
    )
}
