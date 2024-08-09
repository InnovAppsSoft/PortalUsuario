package com.marlon.portalusuario.presentation.mobileservices.components.servsettings

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.presentation.mobileservices.components.servsettings.ServiceSettingsEvent.OnTurnConsumptionRate

@SuppressLint("MissingPermission", "HardwareIds")
@Composable
fun ServiceSettingsView(
    viewModel: ServiceSettingsViewModel = hiltViewModel(),
    mobService: MobileService,
    onSetIsLoading: (Boolean) -> Unit
) {
    onSetIsLoading(viewModel.state.value.isLoading)

    Column {
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
                            viewModel.simCards.firstOrNull { it.slotIndex == mobService.slotIndex },
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
