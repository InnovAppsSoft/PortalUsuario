package com.marlon.portalusuario.feature.mobileservices

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.marlon.portalusuario.core.theme.BrightOrange
import com.marlon.portalusuario.core.theme.BrightSkyBlue
import com.marlon.portalusuario.core.theme.DeepLavender
import com.marlon.portalusuario.core.theme.DeepPurple
import com.marlon.portalusuario.core.theme.TealBlue
import com.marlon.portalusuario.core.theme.TropicalAquamarineGreen
import com.marlon.portalusuario.core.theme.VibrantGreen
import com.marlon.portalusuario.core.theme.VibrantPink
import com.marlon.portalusuario.core.theme.VibrantTangerineOrange
import com.marlon.portalusuario.core.theme.VividRed

private data class ServiceItem(
    val id: String,
    val label: String,
    val color: Color,
    val action: ActionType,
)

private sealed class ActionType {
    data class USSD(
        val code: String,
    ) : ActionType()

    data class Navigate(
        val route: String,
    ) : ActionType()

    data object PlanAmigos : ActionType()
}

@Suppress("LongMethod")
@Composable
fun ServiciosScreen(onNavigate: (String) -> Unit = {}) {
    val context = LocalContext.current
    var rechargeCode by remember { mutableStateOf("") }
    var advanceAmount by remember { mutableStateOf("") }
    var transferPhone by remember { mutableStateOf("") }
    var transferPin by remember { mutableStateOf("") }
    var transferAmount by remember { mutableStateOf("") }
    var showPlanAmigos by remember { mutableStateOf(false) }

    val callPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    val qrScannerLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            result.data?.getStringExtra("scaneo")?.let { rechargeCode = it }
        }

    val contactPickerLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            result.data?.dataString?.let { transferPhone = it }
        }

    val services =
        listOf(
            ServiceItem(
                "saldo",
                "Saldo",
                DeepLavender,
                ActionType.USSD("*222%23"),
            ),
            ServiceItem(
                "datos",
                "Datos",
                TropicalAquamarineGreen,
                ActionType.USSD("*222*328%23"),
            ),
            ServiceItem(
                "llamar99",
                "Llamar *99",
                BrightOrange,
                ActionType.Navigate("call_for_reverse_charge"),
            ),
            ServiceItem(
                "llamarPrivado",
                "Llamar\nPrivado",
                VibrantPink,
                ActionType.Navigate("private_call"),
            ),
            ServiceItem(
                "pospago",
                "Pospago",
                TealBlue,
                ActionType.USSD("*111%23"),
            ),
            ServiceItem(
                "bonos",
                "Bonos",
                VibrantTangerineOrange,
                ActionType.USSD("*222*266%23"),
            ),
            ServiceItem(
                "planAmigos",
                "Plan\nAmigo",
                DeepPurple,
                ActionType.PlanAmigos,
            ),
            ServiceItem("sms", "Plan SMS", BrightSkyBlue, ActionType.Navigate("sms")),
            ServiceItem("voz", "Plan Voz", VibrantGreen, ActionType.Navigate("voz")),
            ServiceItem(
                "emergencia",
                "Emergencias",
                VividRed,
                ActionType.Navigate("emergency_calls"),
            ),
        )

    fun ussdCall(code: String) {
        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE,
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
            text = "Consultar",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(560.dp),
        ) {
            items(services) { service ->
                ServiceCard(
                    label = service.label,
                    color = service.color,
                    onClick = {
                        when (val action = service.action) {
                            is ActionType.USSD -> ussdCall(action.code)
                            is ActionType.Navigate -> onNavigate(action.route)
                            is ActionType.PlanAmigos -> showPlanAmigos = true
                        }
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Recarga", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = rechargeCode,
                        onValueChange = { rechargeCode = it },
                        label = { Text("Código de recarga") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = {
                        qrScannerLauncher.launch(
                            Intent("com.google.zxing.client.android.SCAN"),
                        )
                    }) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Escanear QR",
                        )
                    }
                    IconButton(onClick = {
                        if (rechargeCode.isNotBlank()) {
                            ussdCall("*662*$rechargeCode%23")
                        } else {
                            Toast
                                .makeText(
                                    context,
                                    "Escriba el código de la recarga",
                                    Toast.LENGTH_SHORT,
                                ).show()
                        }
                    }) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Adelantar Saldo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = advanceAmount,
                        onValueChange = { advanceAmount = it },
                        label = { Text("25 o 50 CUP") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = {
                        if (advanceAmount.isNotBlank()) {
                            ussdCall("*234*3*1*$advanceAmount%23")
                        } else {
                            Toast
                                .makeText(
                                    context,
                                    "Escriba 25 o 50 CUP",
                                    Toast.LENGTH_SHORT,
                                ).show()
                        }
                    }) {
                        Icon(Icons.Default.Send, contentDescription = "Adelantar")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Transferir Saldo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = transferPhone,
                    onValueChange = { transferPhone = it },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            val intent =
                                Intent(
                                    Intent.ACTION_PICK,
                                    Uri.parse("content://contacts"),
                                )
                            intent.type = "vnd.android.cursor.dir/phone_v2"
                            contactPickerLauncher.launch(intent)
                        }) {
                            Icon(
                                Icons.Default.Contacts,
                                contentDescription = "Contactos",
                            )
                        }
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = transferPin,
                    onValueChange = { transferPin = it },
                    label = { Text("Clave") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = transferAmount,
                        onValueChange = { transferAmount = it },
                        label = { Text("Monto") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            when {
                                transferPhone.isBlank() ||
                                    transferPin.isBlank() ||
                                    transferAmount.isBlank() ->
                                    Toast
                                        .makeText(
                                            context,
                                            "Todos los campos son obligatorios",
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                else ->
                                    ussdCall(
                                        "*234*1*$transferPhone*$transferPin*$transferAmount%23",
                                    )
                            }
                        },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                            ),
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Enviar")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showPlanAmigos) {
        PlanAmigosDialog(
            onDismiss = { showPlanAmigos = false },
            onUssd = { ussdCall(it) },
        )
    }
}

@Composable
private fun ServiceCard(
    label: String,
    color: Color,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun PlanAmigosDialog(
    onDismiss: () -> Unit,
    onUssd: (String) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Plan Amigos") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        onUssd("*133*4*1%23")
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = DeepLavender,
                        ),
                ) {
                    Text("Activar")
                }
                Button(
                    onClick = {
                        onUssd("*133*4*2%23")
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = TealBlue,
                        ),
                ) {
                    Text("Adicionar/Eliminar")
                }
                Button(
                    onClick = {
                        onUssd("*133*4*3%23")
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = VibrantGreen,
                        ),
                ) {
                    Text("Consultar")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        },
    )
}
