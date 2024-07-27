package com.marlon.portalusuario.presentation.mobileservices.components.servsettings

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.domain.model.SimPaired
import com.marlon.portalusuario.presentation.mobileservices.components.servsettings.ServiceSettingsEvent.OnPairSimCard
import com.marlon.portalusuario.presentation.mobileservices.components.servsettings.ServiceSettingsEvent.OnTurnConsumptionRate
import io.github.suitetecsa.sdk.android.model.SimCard

@SuppressLint("MissingPermission", "HardwareIds")
@Composable
fun ServiceSettingsView(
    viewModel: ServiceSettingsViewModel = hiltViewModel(),
    mobService: MobileService,
    onSetIsLoading: (Boolean) -> Unit
) {
    val preferences by viewModel.preferences.collectAsState()

    onSetIsLoading(viewModel.state.value.isLoading)

    Column {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            PairSimCard(
                mobService = mobService,
                simCards = viewModel.simCards,
                simsPaired = preferences.simsPaired,
                isLoading = false,
                onEvent = viewModel::onEvent
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(text = "Estado")
            Spacer(modifier = Modifier.width(4.dp))
            HorizontalDivider()
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(text = "Tarifa por Consumo")
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = mobService.consumptionRate,
                onCheckedChange = {
                    viewModel.onEvent(
                        OnTurnConsumptionRate(
                            viewModel.simCards.firstOrNull {
                                preferences.simsPaired.firstOrNull { paired ->
                                    paired.serviceId == mobService.id
                                }?.simId == it.telephony.subscriberId
                            },
                            mobService
                        )
                    )
                },
                enabled = !viewModel.state.value.isLoading
            )
        }
        Spacer(modifier = Modifier.height(64.dp))
    }
}

@SuppressLint("MissingPermission", "HardwareIds")
@Composable
fun PairSimCard(
    mobService: MobileService,
    simCards: List<SimCard>,
    simsPaired: List<SimPaired>,
    isLoading: Boolean,
    onEvent: (ServiceSettingsEvent) -> Unit
) {
    val options = mutableListOf<PairedOption>()
    options.addAll(
        simCards.map { card ->
            val displayOption = card.displayName?.let { "$it (SIM ${card.slotIndex + 1})" }
                ?: "(${card.slotIndex + 1})"
            PairedOption(card, displayOption)
        }
    )
    options.add(PairedOption(null, "Ninguna"))

    val selectedOption = simsPaired.firstOrNull { mobService.id == it.serviceId }?.let { paired ->
        options.firstOrNull { it.simCard?.telephony?.subscriberId == paired.simId }
    } ?: options.last()

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(text = "Vincular sim")
            Spacer(modifier = Modifier.width(4.dp))
            HorizontalDivider()
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(options) { item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = item == selectedOption,
                        onClick = { onEvent(OnPairSimCard(item.simCard, mobService)) },
                        enabled = !isLoading
                    )
                    Text(text = item.displayOption)
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }
    }
}

data class PairedOption(val simCard: SimCard?, val displayOption: String)
