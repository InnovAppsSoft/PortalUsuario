package com.marlon.portalusuario.feature.telephony.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.marlon.portalusuario.core.theme.BrightSkyBlue
import com.marlon.portalusuario.core.theme.DeepLavender
import com.marlon.portalusuario.core.theme.TealBlue
import com.marlon.portalusuario.core.theme.VibrantGreen

@Composable
fun SmsScreen() {
    val context = LocalContext.current
    val callPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Planes SMS",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        UssdButton("Plan 20 SMS", "*133*2*1%23", BrightSkyBlue, context, callPermissionLauncher)
        UssdButton("Plan 35 SMS", "*133*2*2%23", TealBlue, context, callPermissionLauncher)
        UssdButton("Plan 45 SMS", "*133*2*3%23", DeepLavender, context, callPermissionLauncher)
        UssdButton("Plan 100 SMS", "*133*2*4%23", VibrantGreen, context, callPermissionLauncher)
    }
}

@Composable
private fun UssdButton(
    label: String,
    code: String,
    color: androidx.compose.ui.graphics.Color,
    context: android.content.Context,
    launcher: androidx.activity.result.ActivityResultLauncher<String>,
) {
    Button(
        onClick = {
            val denied =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CALL_PHONE,
                ) == PackageManager.PERMISSION_DENIED
            if (denied) {
                launcher.launch(Manifest.permission.CALL_PHONE)
            } else {
                context.startActivity(
                    Intent(Intent.ACTION_CALL).apply { data = Uri.parse("tel:$code") },
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(vertical = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
