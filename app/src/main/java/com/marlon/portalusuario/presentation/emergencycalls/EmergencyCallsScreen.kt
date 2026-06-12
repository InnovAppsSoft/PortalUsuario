package com.marlon.portalusuario.presentation.emergencycalls

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.marlon.portalusuario.ui.theme.BrightOrange
import com.marlon.portalusuario.ui.theme.TealBlue
import com.marlon.portalusuario.ui.theme.VibrantGreen
import com.marlon.portalusuario.ui.theme.VibrantPink
import com.marlon.portalusuario.ui.theme.VibrantTangerineOrange
import com.marlon.portalusuario.ui.theme.VividRed

@Composable
fun EmergencyCallsScreen() {
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
            text = "Llamadas de Emergencia",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        EmergencyCard("Ambulancia", "104", VividRed) {
            launchCall(context, "104", callPermissionLauncher)
        }
        EmergencyCard("Policía", "106", TealBlue) {
            launchCall(context, "106", callPermissionLauncher)
        }
        EmergencyCard("Bomberos", "105", VibrantTangerineOrange) {
            launchCall(context, "105", callPermissionLauncher)
        }
        EmergencyCard("Antidrogas", "103", VibrantGreen) {
            launchCall(context, "103", callPermissionLauncher)
        }
        EmergencyCard("Rescate Marítimo", "107", VibrantPink) {
            launchCall(context, "107", callPermissionLauncher)
        }
        EmergencyCard("Cubacel", "52642266", BrightOrange) {
            launchCall(context, "52642266", callPermissionLauncher)
        }
    }
}

@Composable
private fun EmergencyCard(
    label: String,
    number: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = number,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun launchCall(
    context: android.content.Context,
    number: String,
    launcher: androidx.activity.result.ActivityResultLauncher<String>,
) {
    val denied =
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.CALL_PHONE,
        ) == PackageManager.PERMISSION_DENIED
    if (denied) {
        launcher.launch(Manifest.permission.CALL_PHONE)
    } else {
        context.startActivity(
            Intent(Intent.ACTION_CALL).apply { data = Uri.parse("tel:$number") },
        )
    }
}
