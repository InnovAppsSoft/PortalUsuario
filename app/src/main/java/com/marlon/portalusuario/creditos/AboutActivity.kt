package com.marlon.portalusuario.creditos

import android.Manifest
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marlon.portalusuario.R
import kotlinx.coroutines.launch

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val versionName = getAppVersion()
            AboutScreen(versionName, onBack = { finish() })
        }
    }

    private fun getAppVersion(): String {
        return try {
            val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
            pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "N/A"
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AboutScreen(versionName: String, onBack: () -> Unit) {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
    )
    val coroutineScope = rememberCoroutineScope()

    var selectedDeveloper by remember { mutableStateOf("Marlon") }
    val developerPhoneMap = mapOf(
        "Marlon" to "58076608",
        "Lesly" to "54871663"
    )

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            DonationBottomSheet(
                developerName = selectedDeveloper,
                phoneNumber = developerPhoneMap[selectedDeveloper] ?: "",
                onDonate = { amount, password ->
                    // Handle donation logic here
                },
                onDismiss = {
                    coroutineScope.launch {
                        bottomSheetScaffoldState.bottomSheetState.collapse()
                    }
                }
            )
        },
        sheetPeekHeight = 0.dp,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF3E5F5)) // Background color similar to light purple
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                TopBar(onBack = onBack)
                Spacer(modifier = Modifier.height(8.dp))
                ProfileCard(
                    imageRes = R.drawable.marlon,
                    name = stringResource(R.string.marlon_de_jes_s_milan_s_rivero),
                    role = stringResource(R.string.creador_y_desarrollador),
                    onDonateClick = {
                        selectedDeveloper = "Marlon"
                        coroutineScope.launch {
                            bottomSheetScaffoldState.bottomSheetState.expand()
                        }
                    }
                )
                ProfileCard(
                    imageRes = R.drawable.lesly,
                    name = "Lesly Cintra",
                    role = "Desarrollador",
                    onDonateClick = {
                        selectedDeveloper = "Lesly"
                        coroutineScope.launch {
                            bottomSheetScaffoldState.bottomSheetState.expand()
                        }
                    }
                )
                ExperienceCard(versionName)
                ReviewCard()
            }
        }
    )
}

@Composable
fun TopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.app_name),
                tint = Color(0xFF7E57C2), // Light purple color
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ProfileCard(imageRes: Int, name: String, role: String, onDonateClick: () -> Unit) {
    Card(
        backgroundColor = Color(0xFFF3E5F5), // Lighter purple background color for card
        shape = RoundedCornerShape(16.dp), // More rounded corners
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF311B92), // Dark purple color for text
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = role,
                fontSize = 13.sp,
                color = Color(0xFF311B92), // Dark purple color for text
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(10.dp))
            TextButton(
                onClick = onDonateClick,
                modifier = Modifier
                    .background(
                        color = Color(0xFFD1C4E9),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 2.dp, vertical = 0.dp)
            ) {
                Text(
                    text = "Realizar una donación",
                    fontSize = 12.sp,
                    color = Color(0xFF311B92) // Dark purple color for text
                )
            }
        }
    }
}

@Composable
fun DonationBottomSheet(
    developerName: String,
    phoneNumber: String,
    onDonate: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Donación a $developerName",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF311B92) // Dark purple color for text
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Donaciones vía saldo móvil",
            fontSize = 13.sp,
            color = Color(0xFF311B92) // Dark purple color for text
        )
        Spacer(modifier = Modifier.height(16.dp))
        var amount by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Monto") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                onDonate(amount, password)
                val intent = Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:*234*1*$phoneNumber*$amount*$password%23")
                }
                if (context.checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    context.startActivity(intent)
                } else {
                    // Request permission
                }
                onDismiss()
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF7E57C2)), // Light purple button color
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Enviar", color = Color.White)
        }
    }
}

@Composable
fun ExperienceCard(versionName: String) {
    Card(
        backgroundColor = Color(0xFFF3E5F5), // Light purple background color for card
        shape = RoundedCornerShape(16.dp), // More rounded corners
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.acerca_de_la_app),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF311B92) // Dark purple color for text
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_code_24),
                    contentDescription = null,
                    tint = Color(0xFF311B92), // Dark purple color for icon
                    modifier = Modifier.size(25.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = versionName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF311B92) // Dark purple color for text
                    )
                    Text(
                        text = stringResource(R.string.versi_n),
                        fontSize = 13.sp,
                        color = Color(0xFF311B92) // Dark purple color for text
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.hecha_con_amor),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF311B92), // Dark purple color for text
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun ReviewCard() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Card(
        backgroundColor = Color(0xFFD1C4E9), // Light purple background color for card
        shape = RoundedCornerShape(16.dp), // More rounded corners
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(R.string.descargar),
                    fontSize = 16.sp,
                    color = Color(0xFF311B92) // Dark purple color for text
                )
            }
            IconButton(onClick = {
                coroutineScope.launch {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.marlon.portalusuario")
                    context.startActivity(intent)
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    AboutScreen(versionName = "7.0.7", onBack = {})
}









