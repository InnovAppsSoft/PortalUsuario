@file:Suppress("LongMethod")

package com.marlon.portalusuario.une

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.marlon.portalusuario.ui.components.EmptyState
import com.marlon.portalusuario.ui.components.ErrorState
import com.marlon.portalusuario.ui.theme.VibrantGreen
import com.marlon.portalusuario.util.Util
import com.marlon.portalusuario.viewmodel.UneViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UneScreen(
    onBack: () -> Unit,
    viewModel: UneViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allUnes by viewModel.allUnes.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Consumo Eléctrico") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleBottomSheet() }) {
                        Icon(Icons.Default.Info, contentDescription = "Resumen")
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
            )
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Consumo Eléctrico",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedTextField(
                            value = uiState.previousReading,
                            onValueChange = { viewModel.updatePreviousReading(it) },
                            label = { Text("Lectura Anterior") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                        OutlinedTextField(
                            value = uiState.currentReading,
                            onValueChange = { viewModel.updateCurrentReading(it) },
                            label = { Text("Lectura Actual") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.clear() },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Borrar")
                        }
                        Button(
                            onClick = { viewModel.calculate() },
                            modifier = Modifier.weight(1f),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                ),
                        ) {
                            Text("Calcular")
                        }
                    }

                    uiState.errorMessage?.let { error ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                        )
                    }

                    uiState.consumption?.let { consumption ->
                        uiState.totalToPay?.let { total ->
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Consumo total: $consumption Kwh",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = "Usted debe pagar: $$total Pesos",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = VibrantGreen,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Historial",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (allUnes.isEmpty()) {
                EmptyState(
                    message = "No hay registros aún",
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(allUnes.sortedByDescending { it.date }) { une ->
                        UneHistoryItem(une = une)
                    }
                }
            }
        }
    }

    if (uiState.showBottomSheet) {
        val totalConsumption = allUnes.sumOf { it.totalConsumption }
        val totalToPay = allUnes.sumOf { it.totalToPay }

        UneBottomSheet(
            totalConsumption = totalConsumption,
            totalToPay = totalToPay,
            onDismiss = { viewModel.dismissBottomSheet() },
        )
    }
}

@Composable
private fun UneHistoryItem(une: Une) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = une.dateToString(),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Lectura anterior: ${Util.roundDouble(une.lastRegister)}",
                fontSize = 14.sp,
            )
            Text(
                text = "Lectura actual: ${Util.roundDouble(une.currentRegister)}",
                fontSize = 14.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Consumo: ${une.totalConsumption} Kwh",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "$${une.totalToPay}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = VibrantGreen,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UneBottomSheet(
    totalConsumption: Double,
    totalToPay: Double,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
        ) {
            Text(
                text = "Resumen",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp),
            )
            Text(
                text = "Consumo total: ${Util.roundDouble(totalConsumption)} Kw/h",
                fontSize = 18.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Importe total: $${Util.roundDouble(totalToPay)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = VibrantGreen,
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
