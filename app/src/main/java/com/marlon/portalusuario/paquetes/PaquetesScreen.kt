package com.marlon.portalusuario.paquetes

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.marlon.portalusuario.ui.theme.BrightOrange
import com.marlon.portalusuario.ui.theme.BrightSkyBlue
import com.marlon.portalusuario.ui.theme.DeepLavender
import com.marlon.portalusuario.ui.theme.TealBlue
import com.marlon.portalusuario.ui.theme.VibrantGreen
import com.marlon.portalusuario.ui.theme.VibrantTangerineOrange
import com.marlon.portalusuario.ui.theme.VividRed

@Suppress("LongMethod")
@Composable
fun PaquetesScreen() {
    val context = LocalContext.current

    val callPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    fun ussdCall(code: String) {
        if (
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CALL_PHONE,
            ) == PackageManager.PERMISSION_DENIED
        ) {
            callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        } else {
            context.startActivity(
                Intent(Intent.ACTION_CALL).apply { data = Uri.parse("tel:$code") },
            )
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
    ) {
        Text(
            text = "Planes y Paquetes",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle("Paquetes Combinados")
        Spacer(modifier = Modifier.height(8.dp))

        PackageCard(
            title = "Plan Básico",
            price = "110 CUP",
            details =
                listOf(
                    "800MB todas las redes",
                    "600MB LTE",
                    "300MB bono nacional",
                    "15 min",
                    "20 SMS",
                ),
            accentColor = DeepLavender,
            onBuy = { ussdCall("*133*5*1%23") },
        )

        Spacer(modifier = Modifier.height(8.dp))

        PackageCard(
            title = "Plan Medio",
            price = "250 CUP",
            details =
                listOf(
                    "1.5GB todas las redes",
                    "2GB LTE",
                    "300MB bono nacional",
                    "35 min",
                    "40 SMS",
                ),
            accentColor = TealBlue,
            onBuy = { ussdCall("*133*5*2%23") },
        )

        Spacer(modifier = Modifier.height(8.dp))

        PackageCard(
            title = "Plan Extra",
            price = "500 CUP",
            details =
                listOf(
                    "3.5GB todas las redes",
                    "4.5GB LTE",
                    "300MB bono nacional",
                    "75 min",
                    "80 SMS",
                ),
            accentColor = VibrantTangerineOrange,
            onBuy = { ussdCall("*133*5*3%23") },
        )

        Spacer(modifier = Modifier.height(16.dp))
        SectionTitle("Paquetes LTE")
        Spacer(modifier = Modifier.height(8.dp))

        SimplePackageCard(
            title = "1GB LTE",
            price = "100 CUP",
            description = "1GB + 300MB.CU · 30 días",
            accentColor = BrightSkyBlue,
            onBuy = { ussdCall("*133*1*4*1%23") },
        )

        Spacer(modifier = Modifier.height(8.dp))

        SimplePackageCard(
            title = "2.5GB LTE",
            price = "200 CUP",
            description = "2.5GB + 300MB.CU · 30 días",
            accentColor = VibrantGreen,
            onBuy = { ussdCall("*133*1*4*2%23") },
        )

        Spacer(modifier = Modifier.height(8.dp))

        SimplePackageCard(
            title = "4GB+12GB LTE",
            price = "950 CUP",
            description = "4GB + 12GB(LTE) · 30 días",
            accentColor = BrightOrange,
            onBuy = { ussdCall("*133*1*4*3%23") },
        )

        Spacer(modifier = Modifier.height(16.dp))
        SectionTitle("Más opciones")
        Spacer(modifier = Modifier.height(8.dp))

        SimplePackageCard(
            title = "Bolsa Diaria LTE",
            price = "25 CUP",
            description = "200MB · 24h",
            accentColor = VividRed,
            onBuy = { ussdCall("*133*1*3%23") },
        )

        Spacer(modifier = Modifier.height(8.dp))

        SimplePackageCard(
            title = "Bolsa de Mensajería",
            price = "25 CUP",
            description = "600MB · 30 días",
            accentColor = DeepLavender,
            onBuy = { ussdCall("*133*1*2%23") },
        )

        Spacer(modifier = Modifier.height(8.dp))

        SimplePackageCard(
            title = "Tarifa por Consumo",
            price = "0.50 CUP",
            description = "Activación de tarifa por consumo",
            accentColor = BrightSkyBlue,
            onBuy = { ussdCall("*133*1*1%23") },
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun PackageCard(
    title: String,
    price: String,
    details: List<String>,
    accentColor: Color,
    onBuy: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
                Text(
                    text = price,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = accentColor,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            details.forEach { detail ->
                Text(
                    text = "• $detail",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onBuy,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
            ) {
                Text(
                    text = "Comprar $price",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun SimplePackageCard(
    title: String,
    price: String,
    description: String,
    accentColor: Color,
    onBuy: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = price,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = accentColor,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = onBuy,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
            ) {
                Text(
                    text = "Comprar",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        }
    }
}
